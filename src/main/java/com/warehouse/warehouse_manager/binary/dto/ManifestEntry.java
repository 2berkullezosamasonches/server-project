package com.warehouse.warehouse_manager.binary.dto;

import java.util.UUID;

public class ManifestEntry {

    private UUID id;
    private int statusCode;
    private long updatedAt;

    private long dataOffset;
    private long dataLength;

    private byte[] signatureBytes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public long getDataOffset() { return dataOffset; }
    public void setDataOffset(long dataOffset) { this.dataOffset = dataOffset; }

    public long getDataLength() { return dataLength; }
    public void setDataLength(long dataLength) { this.dataLength = dataLength; }

    public byte[] getSignatureBytes() { return signatureBytes; }
    public void setSignatureBytes(byte[] signatureBytes) { this.signatureBytes = signatureBytes; }
}