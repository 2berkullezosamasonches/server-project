package com.warehouse.warehouse_manager.binary.dto;

import java.util.List;

public class BinaryDataBuildResult {

    private byte[] data;
    private List<ManifestEntry> entries;

    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public List<ManifestEntry> getEntries() { return entries; }
    public void setEntries(List<ManifestEntry> entries) { this.entries = entries; }
}