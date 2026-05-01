package com.warehouse.warehouse_manager.binary.dto;

import java.util.UUID;

public class BinarySignatureRecord {

    private UUID id;
    private int statusCode;
    private long updatedAt;

    private String threatName;
    private byte[] firstBytes;
    private byte[] remainderHash;
    private long remainderLength; // ← ИСПРАВЛЕНО (было int)
    private String fileType;
    private long offsetStart;
    private long offsetEnd;

    private byte[] signatureBytes;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getThreatName() { return threatName; }
    public void setThreatName(String threatName) { this.threatName = threatName; }

    public byte[] getFirstBytes() { return firstBytes; }
    public void setFirstBytes(byte[] firstBytes) { this.firstBytes = firstBytes; }

    public byte[] getRemainderHash() { return remainderHash; }
    public void setRemainderHash(byte[] remainderHash) { this.remainderHash = remainderHash; }

    public long getRemainderLength() { return remainderLength; } // ← ИСПРАВЛЕНО
    public void setRemainderLength(long remainderLength) { this.remainderLength = remainderLength; } // ← ИСПРАВЛЕНО

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getOffsetStart() { return offsetStart; }
    public void setOffsetStart(long offsetStart) { this.offsetStart = offsetStart; }

    public long getOffsetEnd() { return offsetEnd; }
    public void setOffsetEnd(long offsetEnd) { this.offsetEnd = offsetEnd; }

    public byte[] getSignatureBytes() { return signatureBytes; }
    public void setSignatureBytes(byte[] signatureBytes) { this.signatureBytes = signatureBytes; }
}