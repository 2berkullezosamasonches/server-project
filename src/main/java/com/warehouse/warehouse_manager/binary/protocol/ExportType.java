package com.warehouse.warehouse_manager.binary.protocol;

public enum ExportType {

    FULL(1),
    INCREMENT(2),
    BY_IDS(3);

    private final int code;

    ExportType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}