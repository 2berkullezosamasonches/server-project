package com.warehouse.warehouse_manager.config;

import com.warehouse.warehouse_manager.model.LicenseType;
import com.warehouse.warehouse_manager.model.Product;
import com.warehouse.warehouse_manager.repository.LicenseTypeRepository;
import com.warehouse.warehouse_manager.repository.ProductRepository;
import com.warehouse.warehouse_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            ProductRepository productRepository,
            LicenseTypeRepository licenseTypeRepository
    ) {
        return args -> {
            // 1. Проверка наличия администратора (просто лог для уверенности)
            if (userRepository.findByUsername("admin").isPresent()) {
                System.out.println(">>> [INIT] Администратор обнаружен в базе данных.");
            } else {
                System.out.println(">>> [WARNING] Администратор 'admin' не найден в базе! Убедитесь, что он был создан ранее.");
            }

            // 2. Инициализация Продуктов (если база пустая)
            if (productRepository.findAll().isEmpty()) {
                Product product = new Product();
                product.setName("Warehouse Management System Pro");
                product.setBlocked(false);
                productRepository.save(product);
                System.out.println(">>> [INIT] Тестовый продукт 'WMS Pro' добавлен.");
            }

            // 3. Инициализация Типов лицензий (если база пустая)
            if (licenseTypeRepository.findAll().isEmpty()) {
                LicenseType annual = new LicenseType();
                annual.setName("Annual Subscription");
                annual.setDefaultDuration(365);
                licenseTypeRepository.save(annual);

                LicenseType trial = new LicenseType();
                trial.setName("Trial Period");
                trial.setDefaultDuration(30);
                licenseTypeRepository.save(trial);

                System.out.println(">>> [INIT] Типы лицензий (Annual, Trial) добавлены.");
            }
        };
    }
}