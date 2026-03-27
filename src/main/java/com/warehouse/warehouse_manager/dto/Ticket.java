package com.warehouse.warehouse_manager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/** * Аннотация ниже — это то, что просил препод.
 * Она говорит Jackson: "Если поле равно null, не рисуй его в JSON вообще".
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime currentServerTime;

    private Integer ticketLifetimeSeconds;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime firstActivationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endingDate;

    private Long userId;
    private Long deviceId;
    private Boolean blocked;

    // Если есть какие-то дополнительные поля, которые могут быть пустыми:
    private String licenseType;
    private String customerName;
}