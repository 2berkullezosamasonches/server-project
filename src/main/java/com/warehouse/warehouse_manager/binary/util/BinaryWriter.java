package com.warehouse.warehouse_manager.binary.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BinaryWriter {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public void writeUInt8(int value) {
        outputStream.write(value & 0xFF);
    }

    public void writeUInt16(int value) {
        outputStream.write((value >>> 8) & 0xFF);
        outputStream.write(value & 0xFF);
    }

    public void writeUInt32(long value) {
        outputStream.write((int) ((value >>> 24) & 0xFF));
        outputStream.write((int) ((value >>> 16) & 0xFF));
        outputStream.write((int) ((value >>> 8) & 0xFF));
        outputStream.write((int) (value & 0xFF));
    }

    public void writeInt64(long value) {
        outputStream.write((int) ((value >>> 56) & 0xFF));
        outputStream.write((int) ((value >>> 48) & 0xFF));
        outputStream.write((int) ((value >>> 40) & 0xFF));
        outputStream.write((int) ((value >>> 32) & 0xFF));
        outputStream.write((int) ((value >>> 24) & 0xFF));
        outputStream.write((int) ((value >>> 16) & 0xFF));
        outputStream.write((int) ((value >>> 8) & 0xFF));
        outputStream.write((int) (value & 0xFF));
    }

    public void writeUuid(UUID uuid) {
        writeInt64(uuid.getMostSignificantBits());
        writeInt64(uuid.getLeastSignificantBits());
    }

    public void writeBytes(byte[] bytes) {
        outputStream.writeBytes(bytes);
    }

    public void writeByteArray(byte[] bytes) {
        writeUInt32(bytes.length);
        writeBytes(bytes);
    }

    public void writeString(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeUInt32(bytes.length);
        writeBytes(bytes);
    }

    public byte[] toByteArray() {
        return outputStream.toByteArray();
    }

    public int size() {
        return outputStream.size();
    }
}