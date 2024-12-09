package com.drbrosdev.klinkrest.domain.dto;


import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkDto {

    UUID id;
    String name;
    String readKey;
    String writeKey;

    @Nullable
    String description;

    List<KlinkEntryDto> entries;
}
