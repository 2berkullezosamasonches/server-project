package com.warehouse.warehouse_manager.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Этот метод перехватит все RuntimeException, которые мы выбрасываем в AuthService
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
                .badRequest() // Статус 400
                .body(Map.of("error", e.getMessage())); // Тело ответа в JSON
    }
}