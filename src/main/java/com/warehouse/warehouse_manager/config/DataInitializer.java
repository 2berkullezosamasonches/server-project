package com.warehouse.warehouse_manager.config;

import com.warehouse.warehouse_manager.model.User;
import com.warehouse.warehouse_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Проверяем, есть ли уже хоть один пользователь
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                // Пароль хешируется ПЕРЕД сохранением
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));

                userRepository.save(admin);
                System.out.println(">>> Начальный администратор создан: admin / Admin123!");
            }
        };
    }
}