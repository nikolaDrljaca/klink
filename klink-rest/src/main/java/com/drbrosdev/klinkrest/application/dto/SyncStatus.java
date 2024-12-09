package com.drbrosdev.klinkrest.application.dto;

public enum SyncStatus {

    IN_SYNC("IN_SYNC"),

    OUT_OF_SYNC("OUT_OF_SYNC");

    private final String value;

    SyncStatus(String value) {
        this.value = value;
    }

    public static SyncStatus fromValue(String syncStatusValue) {
        for (SyncStatus syncStatus : SyncStatus.values()) {
            if (syncStatus.value.equals(syncStatusValue)) {
                return syncStatus;
            }
        }
        throw new IllegalArgumentException("Unexpected value: '" + syncStatusValue + "'");
    }
}
