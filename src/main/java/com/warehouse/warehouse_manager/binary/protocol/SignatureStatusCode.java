package com.warehouse.warehouse_manager.binary.protocol;

public enum SignatureStatusCode {

    ACTUAL(1),
    DELETED(2);

    private final int code;

    SignatureStatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}