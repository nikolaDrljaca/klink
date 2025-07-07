package com.drbrosdev.klinkrest.domain.klink.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkChangeNotification {

    Operation operation;
    Row row;

    @Value
    @Builder(toBuilder = true)
    @AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
    public static class Row {
        String id;
        String name;
        String description;

        @JsonProperty("created_at")
        String createdAt;
        @JsonProperty("modified_at")
        String modifiedAt;
    }
}
