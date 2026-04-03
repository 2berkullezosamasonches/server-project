package com.warehouse.warehouse_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warehouse.warehouse_manager.dto.Ticket;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

@Service
public class CanonicalizationService {

    private final ObjectMapper objectMapper;

    public CanonicalizationService() {
        this.objectMapper = new ObjectMapper();
        // Подключаем модуль для работы с датами (LocalDateTime, Instant)
        this.objectMapper.registerModule(new JavaTimeModule());
        // Отключаем запись дат в виде массивов [2023,10,12...], пишем как строки ISO
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Канонизация для лицензий (Ticket).
     */
    public byte[] canonicalize(Ticket ticket) throws Exception {
        // Конвертируем в TreeMap для автоматической сортировки ключей по алфавиту
        TreeMap<String, Object> sortedMap = objectMapper.convertValue(ticket, TreeMap.class);
        return objectMapper.writeValueAsBytes(sortedMap);
    }

    /**
     * Канонизация для антивирусных сигнатур (MalwareSignature).
     */
    public byte[] canonicalizeSignature(MalwareSignature sig) throws Exception {
        // Используем TreeMap, чтобы ключи всегда шли в порядке алфа
        TreeMap<String, Object> map = new TreeMap<>();

        map.put("threatName", sig.getThreatName());
        map.put("firstBytesHex", sig.getFirstBytesHex());
        map.put("remainderHashHex", sig.getRemainderHashHex());
        map.put("remainderLength", sig.getRemainderLength());
        map.put("fileType", sig.getFileType());
        map.put("offsetStart", sig.getOffsetStart());
        map.put("offsetEnd", sig.getOffsetEnd());
        map.put("status", sig.getStatus().name()); // Статус как строка (ACTUAL/DELETED)

        // Превращаем в компактный JSON без лишних пробелов
        return objectMapper.writeValueAsBytes(map);
    }
}