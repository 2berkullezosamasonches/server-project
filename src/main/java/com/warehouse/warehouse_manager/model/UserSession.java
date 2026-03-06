package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @Column(length = 512) // Access token длинный, увеличиваем лимит
    private String accessToken;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private LocalDateTime accessTokenExpiry;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}