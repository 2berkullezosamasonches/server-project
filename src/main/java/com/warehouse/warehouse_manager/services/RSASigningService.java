package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.Ticket;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class RSASigningService {

    private final KeyProvider keyProvider;
    private final CanonicalizationService canonicalizationService;

    public String signTicket(Ticket ticket) throws Exception {
        // Получаем каноничные байты тикета (уже реализовано у тебя в CanonicalizationService)
        byte[] data = canonicalizationService.canonicalize(ticket);
        return signData(data);
    }

    // Используется в MalwareSignatureService.

    public String signMalwareSignature(MalwareSignature sig) throws Exception {
        // Вызываем новый метод канонизации специально для сигнатур
        byte[] data = canonicalizationService.canonicalizeSignature(sig);
        return signData(data);
    }

    /**
     * Алгоритм: SHA256withRSA.
     */
    private String signData(byte[] data) throws Exception {
        try {
            // Достаем приватный ключ из хранилища (через твой KeyProvider)
            PrivateKey privateKey = keyProvider.getPrivateKey();

            // Инициализируем объект подписи
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privateKey);
            rsa.update(data);

            // Возвращаем результат в формате Base64
            byte[] signatureBytes = rsa.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("Критическая ошибка при создании цифровой подписи RSA: ", e);
            throw new RuntimeException("Не удалось создать цифровую подпись данных", e);
        }
    }
}