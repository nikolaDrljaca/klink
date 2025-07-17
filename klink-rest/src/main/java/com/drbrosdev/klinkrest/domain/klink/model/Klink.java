package com.drbrosdev.klinkrest.domain.klink.model;


import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class Klink {

    UUID id;
    String name;

    @Nullable
    String description;
    LocalDateTime updatedAt;

    KlinkKey key;

    List<KlinkEntry> entries;
}
