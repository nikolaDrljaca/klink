package com.drbrosdev.klinkrest.application.dto;


import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

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

}
