package com.drbrosdev.klinkrest.persistence.entity;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface KlinkFullRow {

    UUID getKlinkId();

    String getKlinkName();

    @Nullable
    String getKlinkDescription();

    LocalDateTime getKlinkCreatedAt();

    LocalDateTime getKlinkModifiedAt();

    String getReadKey();

    String getWriteKey();

    UUID getKlinkEntryId();

    String getKlinkEntry();

    LocalDateTime getEntryCreatedAt();

    @Nullable
    String getKlinkEntryTitle();

    @Nullable
    String getKlinkEntryDescription();
}
