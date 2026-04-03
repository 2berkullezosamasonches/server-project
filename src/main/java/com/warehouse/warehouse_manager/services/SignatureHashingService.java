package com.warehouse.warehouse_manager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.warehouse_manager.dto.SignaturePayload;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class SignatureHashingService {

    private final ObjectMapper objectMapper;
    private final RSASigningService rsaSigningService;

    public String generateDigitalSignature(MalwareSignature sig) throws Exception {
        // 1. Собираем объект только с нужными полями
        SignaturePayload payload = SignaturePayload.builder()
                .threatName(sig.getThreatName())
                .firstBytesHex(sig.getFirstBytesHex())
                .remainderHashHex(sig.getRemainderHashHex())
                .remainderLength(sig.getRemainderLength())
                .fileType(sig.getFileType())
                .offsetStart(sig.getOffsetStart())
                .offsetEnd(sig.getOffsetEnd())
                .status(sig.getStatus())
                .build();

        // 2. КАНОНИЗАЦИЯ (JCS):
        // Конвертируем в TreeMap, чтобы ключи автоматически встали по алфавиту (a-z)
        TreeMap<String, Object> sortedMap = objectMapper.convertValue(payload, TreeMap.class);

        // 3. Превращаем в JSON-строку
        // Jackson в твоем конфиге уже настроен не писать null и писать числа корректно
        String canonicalJson = objectMapper.writeValueAsString(sortedMap);

        // 4. Отправляем на подпись в твой RSA сервис
        return rsaSigningService.signMalwareSignature(sig);
    }
}