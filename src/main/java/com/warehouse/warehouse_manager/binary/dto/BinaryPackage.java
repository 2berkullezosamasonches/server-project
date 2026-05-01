package com.warehouse.warehouse_manager.binary.dto;

public class BinaryPackage {

    private byte[] manifest;
    private byte[] data;

    public byte[] getManifest() {
        return manifest;
    }

    public void setManifest(byte[] manifest) {
        this.manifest = manifest;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}