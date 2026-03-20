package com.warehouse.warehouse_manager.security;

import com.warehouse.warehouse_manager.dto.Ticket;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class TicketSigner {

    // Секретный ключ. В реальном проекте хранится в конфигах.
    @Value("${ticket.signature.secret:MySecretKeyForSigningTickets2024}")
    private String secretKey;

    private SecretKeySpec keySpec;

    @PostConstruct
    public void init() {
        this.keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    // Создает подпись на основе данных тикета
    public String sign(Ticket ticket) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);

            // Собираем значимые поля в одну строку для подписи
            String dataToSign = String.format("%d|%d|%s|%b",
                    ticket.getUserId(),
                    ticket.getDeviceId(),
                    ticket.getEndingDate().toString(),
                    ticket.isBlocked());

            byte[] rawHmac = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Ошибка при создании подписи тикета", e);
        }
    }
}