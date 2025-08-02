package com.drbrosdev.klinkrest.domain.klink.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkSessionDetails {
    UUID klinkId;
    String readKey;
}
