package com.warehouse.warehouse_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Ticket ticket;   // Данные
    private String signature; // ЭЦП (хэш-сумма, созданная секретным ключом)
}