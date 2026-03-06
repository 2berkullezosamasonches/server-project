package com.warehouse.warehouse_manager.model;

public enum SessionStatus {
    ACTIVE,   // Токен годен для обновления
    USED,     // Токен уже был использован для получения новой пары (повторно нельзя)
    REVOKED   // Токен отозван (например, при подозрении на кражу или логауте)
}