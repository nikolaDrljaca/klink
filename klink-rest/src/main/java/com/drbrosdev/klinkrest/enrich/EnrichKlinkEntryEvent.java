package com.drbrosdev.klinkrest.enrich;

import lombok.Builder;

import java.util.UUID;

@Builder
public record EnrichKlinkEntryEvent(
        UUID klinkId,
        UUID klinkEntryId,
        String value) {
}
