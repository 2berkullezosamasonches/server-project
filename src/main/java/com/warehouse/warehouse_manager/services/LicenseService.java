package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.Ticket;
import com.warehouse.warehouse_manager.dto.TicketResponse;
import com.warehouse.warehouse_manager.model.*;
import com.warehouse.warehouse_manager.repository.*;
import com.warehouse.warehouse_manager.security.TicketSigner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final ProductRepository productRepository;
    private final LicenseTypeRepository licenseTypeRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceLicenseRepository deviceLicenseRepository;
    private final LicenseHistoryRepository licenseHistoryRepository;
    private final TicketSigner ticketSigner;

    @Transactional
    public License createLicense(Long productId, Long ownerId, Long typeId, Integer deviceCount) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Продукт не найден"));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Владелец не найден"));
        LicenseType type = licenseTypeRepository.findById(typeId).orElseThrow(() -> new RuntimeException("Тип лицензии не найден"));

        License license = License.builder()
                .code(UUID.randomUUID().toString())
                .product(product).owner(owner).licenseType(type)
                .deviceCount(deviceCount).blocked(false).build();

        return recordHistory(licenseRepository.save(license), owner, "CREATED", "Лицензия создана");
    }

    @Transactional
    public TicketResponse activateLicense(String code, String deviceMac, String deviceName, User user) {
        License license = licenseRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Лицензия не найдена"));

        if (Boolean.TRUE.equals(license.getBlocked())) throw new RuntimeException("Лицензия заблокирована");

        // Проверка лимита устройств (Пункт 08 вашего плана)
        long currentCount = deviceLicenseRepository.countByLicense(license);
        System.out.println(">>> [DEBUG] Лицензия: " + code + " | Текущих устройств: " + currentCount + " | Лимит: " + license.getDeviceCount());

        // Если это новое устройство и лимит уже достигнут - выбрасываем 409
        boolean isAlreadyLinked = deviceLicenseRepository.findAll().stream()
                .anyMatch(dl -> dl.getLicense().getId().equals(license.getId()) && dl.getDevice().getMacAddress().equals(deviceMac));

        if (!isAlreadyLinked && currentCount >= license.getDeviceCount()) {
            throw new IllegalStateException("DEVICE_LIMIT_EXCEEDED");
        }

        Device device = deviceRepository.findByMacAddress(deviceMac).orElseGet(() -> {
            Device newDevice = new Device();
            newDevice.setMacAddress(deviceMac);
            newDevice.setName(deviceName);
            newDevice.setUser(user);
            return deviceRepository.save(newDevice);
        });

        if (!isAlreadyLinked) {
            DeviceLicense dl = new DeviceLicense();
            dl.setLicense(license); dl.setDevice(device); dl.setActivationDate(LocalDateTime.now());
            deviceLicenseRepository.save(dl);
        }

        if (license.getFirstActivationDate() == null) {
            license.setFirstActivationDate(LocalDateTime.now());
            license.setEndingDate(LocalDateTime.now().plusDays(license.getLicenseType().getDefaultDuration()));
            license.setUser(user);
        }

        recordHistory(licenseRepository.save(license), user, "ACTIVATED", "Устройство: " + deviceName);
        return generateTicketResponse(license, device, user);
    }

    @Transactional
    public TicketResponse renewLicense(String code, User user) {
        License license = licenseRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Лицензия не найдена"));

        if (license.getUser() == null || !license.getUser().getId().equals(user.getId()))
            throw new RuntimeException("Вы не владелец этой лицензии");

        // Пункт 10: Раннее продление (409 Conflict)
        if (license.getEndingDate() != null && license.getEndingDate().isAfter(LocalDateTime.now().plusDays(30))) {
            throw new IllegalStateException("TOO_EARLY_FOR_RENEWAL");
        }

        LocalDateTime baseDate = (license.getEndingDate() == null || license.getEndingDate().isBefore(LocalDateTime.now()))
                ? LocalDateTime.now() : license.getEndingDate();

        license.setEndingDate(baseDate.plusDays(license.getLicenseType().getDefaultDuration()));
        recordHistory(licenseRepository.save(license), user, "RENEWED", "Продлено до: " + license.getEndingDate());

        Device lastDevice = deviceLicenseRepository.findAll().stream()
                .filter(dl -> dl.getLicense().getId().equals(license.getId()))
                .map(DeviceLicense::getDevice).findFirst().orElse(null);

        return generateTicketResponse(license, lastDevice, user);
    }

    public TicketResponse checkLicense(String deviceMac, Long productId, User user) {
        Device device = deviceRepository.findByMacAddress(deviceMac).orElseThrow(() -> new RuntimeException("Устройство не найдено"));
        License license = licenseRepository.findAll().stream()
                .filter(l -> l.getUser() != null && l.getUser().getId().equals(user.getId()))
                .filter(l -> l.getProduct().getId().equals(productId) && !l.getBlocked())
                .filter(l -> l.getEndingDate().isAfter(LocalDateTime.now()))
                .findFirst().orElseThrow(() -> new RuntimeException("Лицензия не найдена или истекла"));

        return generateTicketResponse(license, device, user);
    }

    private License recordHistory(License l, User u, String s, String d) {
        LicenseHistory h = new LicenseHistory();
        h.setLicense(l); h.setUser(u); h.setStatus(s); h.setChangeDate(LocalDateTime.now()); h.setDescription(d);
        licenseHistoryRepository.save(h);
        return l;
    }

    private TicketResponse generateTicketResponse(License l, Device d, User u) {
        Ticket t = Ticket.builder()
                .currentServerTime(LocalDateTime.now()).ticketLifetimeSeconds(3600L)
                .firstActivationDate(l.getFirstActivationDate()).endingDate(l.getEndingDate())
                .userId(u.getId()).deviceId(d != null ? d.getId() : null).blocked(l.getBlocked()).build();
        return new TicketResponse(t, ticketSigner.sign(t));
    }
}