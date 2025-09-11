package com.drbrosdev.klinkrest.domain.klink.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class EnrichKlinkEntryJob {
    UUID klinkId;
    UUID klinkEntryId;

    String value;
}
