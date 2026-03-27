package com.warehouse.warehouse_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warehouse.warehouse_manager.dto.Ticket;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

@Service
public class CanonicalizationService {
    private final ObjectMapper objectMapper;

    public CanonicalizationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public byte[] canonicalize(Ticket ticket) throws Exception {
        // Чтобы поля всегда были в одном порядке (алфавитном),
        // конвертируем объект в TreeMap
        String json = objectMapper.writeValueAsString(ticket);
        TreeMap<String, Object> sortedMap = objectMapper.readValue(json, TreeMap.class);

        // Превращаем отсортированную карту обратно в строку без лишних пробелов
        String canonicalJson = objectMapper.writeValueAsString(sortedMap);
        return canonicalJson.getBytes(StandardCharsets.UTF_8);
    }
}