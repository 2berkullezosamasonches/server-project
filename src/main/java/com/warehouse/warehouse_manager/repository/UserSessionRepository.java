package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Поиск сессии по Refresh токену.
     * Используется в TokenService при обновлении пары токенов.
     */
    Optional<UserSession> findByRefreshToken(String refreshToken);

    /**
     * Поиск сессии по Access токену.
     * Используется в JwtAuthenticationFilter для проверки,
     * не был ли этот конкретный токен аннулирован после Refresh.
     */
    Optional<UserSession> findByAccessToken(String accessToken);

    /**
     * Удаление всех сессий пользователя (например, для полного Logout)
     */
    void deleteByUserEmail(String userEmail);
}