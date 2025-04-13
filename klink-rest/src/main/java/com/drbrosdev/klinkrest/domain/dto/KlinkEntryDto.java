package com.drbrosdev.klinkrest.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkEntryDto {
    String value;

    LocalDateTime createdAt;
}
