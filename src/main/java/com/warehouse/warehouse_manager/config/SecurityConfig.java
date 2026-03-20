package com.warehouse.warehouse_manager.config;

import com.warehouse.warehouse_manager.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Позволяет использовать @PreAuthorize в контроллерах
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Отключаем CSRF, так как используем JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Публичные эндпоинты (Логин и обновление токена)
                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()

                        // 2. Управление пользователями (Только ADMIN)
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")

                        // 3. Лицензирование: Создание (Только ADMIN)
                        .requestMatchers("/api/licenses/create").hasRole("ADMIN")

                        // 4. Лицензирование: Активация, Проверка, Продление (Любой авторизованный)
                        .requestMatchers("/api/licenses/activate", "/api/licenses/check", "/api/licenses/renew").authenticated()

                        // 5. Остальные API (Склады, товары и т.д.)
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        // Добавляем наш JWT фильтр перед стандартным фильтром аутентификации
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}