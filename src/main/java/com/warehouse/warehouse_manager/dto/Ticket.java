package com.warehouse.warehouse_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private LocalDateTime currentServerTime;
    private Long ticketLifetimeSeconds;
    private LocalDateTime firstActivationDate;
    private LocalDateTime endingDate;
    private Long userId;
    private Long deviceId;

    // Используем просто blocked, чтобы Builder создал метод .blocked()
    private boolean blocked;
}