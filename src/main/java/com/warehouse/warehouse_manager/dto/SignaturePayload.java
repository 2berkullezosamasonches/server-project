package com.warehouse.warehouse_manager.dto;

import com.warehouse.warehouse_manager.model.SignatureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignaturePayload {

    private String threatName;
    private String firstBytesHex;
    private String remainderHashHex;
    private long remainderLength;
    private String fileType;
    private long offsetStart;
    private long offsetEnd;
    private SignatureStatus status;
}