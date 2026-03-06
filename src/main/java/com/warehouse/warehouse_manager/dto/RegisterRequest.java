package com.warehouse.warehouse_manager.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // Добавляем роль (например, ADMIN или USER)
}