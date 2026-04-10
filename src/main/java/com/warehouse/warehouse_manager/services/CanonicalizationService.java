package com.warehouse.warehouse_manager.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CanonicalizationService {

    private final ObjectMapper objectMapper;

    public CanonicalizationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        this.objectMapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    public byte[] canonicalize(Object payload) {
        try {
            JsonNode root = objectMapper.valueToTree(payload);
            String canonicalJson = canonicalizeNode(root);
            return canonicalJson.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка канонизации payload", e);
        }
    }

    public String canonicalizeToString(Object payload) {
        return new String(canonicalize(payload), StandardCharsets.UTF_8);
    }

    private String canonicalizeNode(JsonNode node) throws JsonProcessingException {
        if (node == null || node.isNull()) {
            return "null";
        }

        if (node.isObject()) {
            List<String> fieldNames = new ArrayList<>();
            Iterator<String> it = node.fieldNames();
            while (it.hasNext()) {
                fieldNames.add(it.next());
            }
            fieldNames.sort(String::compareTo);

            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < fieldNames.size(); i++) {
                String name = fieldNames.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(objectMapper.writeValueAsString(name))
                        .append(":")
                        .append(canonicalizeNode(node.get(name)));
            }
            sb.append("}");
            return sb.toString();
        }

        if (node.isArray()) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < node.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(canonicalizeNode(node.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        if (node.isTextual()) {
            return objectMapper.writeValueAsString(node.textValue());
        }

        if (node.isBoolean()) {
            return String.valueOf(node.booleanValue());
        }

        if (node.isNumber()) {
            return normalizeNumber(node.asText());
        }

        return objectMapper.writeValueAsString(node);
    }

    private String normalizeNumber(String raw) {
        BigDecimal bd = new BigDecimal(raw).stripTrailingZeros();

        if (bd.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        String plain = bd.toPlainString();

        if (plain.matches("-?\\d{21,}")) {
            return bd.toString().replace("E", "e");
        }

        if (plain.matches("-?0\\.0{6,}\\d+")) {
            return bd.toString().replace("E", "e");
        }

        return plain;
    }
}