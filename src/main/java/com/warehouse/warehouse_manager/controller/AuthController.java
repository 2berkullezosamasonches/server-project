package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.dto.JwtResponse;
import com.warehouse.warehouse_manager.dto.LoginRequest;
import com.warehouse.warehouse_manager.model.User;
import com.warehouse.warehouse_manager.repository.UserRepository;
import com.warehouse.warehouse_manager.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private TokenService tokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Регулярное выражение: минимум 8 символов, одна цифра, один спецсимвол
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})";

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        List<String> rolesReq = (List<String>) request.getOrDefault("roles", List.of("USER"));

        // 1. Проверка на пустые поля
        if (username == null || password == null || username.isBlank()) {
            return ResponseEntity.badRequest().body("Имя пользователя и пароль не могут быть пустыми");
        }

        // 2. Валидация сложности пароля
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).find()) {
            return ResponseEntity.badRequest().body(
                    "Пароль слишком простой! Минимум 8 символов, должен содержать цифру и спецсимвол (!@#$%^&*)"
            );
        }

        // 3. Проверка на уникальность логина
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким именем уже существует");
        }

        // 4. Сохранение
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(new HashSet<>(rolesReq))
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            JwtResponse response = tokenService.createSession(user.getUsername(), new ArrayList<>(user.getRoles()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Неверный логин или пароль");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        try {
            return ResponseEntity.ok(tokenService.refreshSession(request.get("refreshToken")));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
}