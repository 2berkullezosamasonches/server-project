package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.JwtResponse;
import com.warehouse.warehouse_manager.model.SessionStatus;
import com.warehouse.warehouse_manager.model.User;
import com.warehouse.warehouse_manager.model.UserSession;
import com.warehouse.warehouse_manager.repository.UserRepository;
import com.warehouse.warehouse_manager.repository.UserSessionRepository;
import com.warehouse.warehouse_manager.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserSessionRepository userSessionRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public JwtResponse createSession(String username, List<String> roles) {
        String accessToken = jwtTokenProvider.createAccessToken(username, roles);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        UserSession session = UserSession.builder()
                .userEmail(username)
                .accessToken(accessToken) // Сохраняем access токен
                .refreshToken(refreshToken)
                .status(SessionStatus.ACTIVE)
                .accessTokenExpiry(LocalDateTime.now().plusMinutes(15))
                .build();

        userSessionRepository.save(session);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional
    public JwtResponse refreshSession(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh токен невалиден");
        }

        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Сессия не найдена"));

        // Если сессия уже была использована (USED), аннулируем всё — это попытка взлома
        if (session.getStatus() != SessionStatus.ACTIVE) {
            session.setStatus(SessionStatus.REVOKED);
            userSessionRepository.save(session);
            throw new RuntimeException("Сессия более не активна. Войдите заново.");
        }

        // Ключевой момент: помечаем старую сессию и её Access Token как ИСПОЛЬЗОВАННЫЕ
        session.setStatus(SessionStatus.USED);
        session.setUpdatedAt(LocalDateTime.now());
        userSessionRepository.save(session);

        User user = userRepository.findByUsername(session.getUserEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return createSession(user.getUsername(), new ArrayList<>(user.getRoles()));
    }
}