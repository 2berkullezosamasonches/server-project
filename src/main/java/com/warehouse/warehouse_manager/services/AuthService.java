package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.model.User;
import com.warehouse.warehouse_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(String username, String password, String role) {
        // 1. Проверка: не занято ли имя пользователя
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Ошибка: Пользователь с таким именем уже существует!");
        }

        // 2. Валидация пароля (согласно заданию)
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Ошибка: Пароль слишком короткий! Минимум 8 символов.");
        }

        // Проверка на наличие спецсимвола через регулярное выражение
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new RuntimeException("Ошибка: Пароль должен содержать хотя бы один спецсимвол (!@#$%^&*...)");
        }

        // 3. Создание нового объекта пользователя
        User user = new User();
        user.setUsername(username);

        // Хешируем пароль (в базе будет не "123", а длинный зашифрованный код)
        user.setPassword(passwordEncoder.encode(password));

        // 4. Определение роли
        // Если в JSON прислали "ADMIN", даем роль админа, иначе — обычный юзер
        if ("ADMIN".equalsIgnoreCase(role)) {
            user.setRoles(Set.of("ROLE_ADMIN"));
        } else {
            user.setRoles(Set.of("ROLE_USER"));
        }

        // 5. Сохранение в базу данных
        userRepository.save(user);

        return "Пользователь " + username + " успешно зарегистрирован с ролью " +
                (user.getRoles().contains("ROLE_ADMIN") ? "ADMIN" : "USER");
    }
}