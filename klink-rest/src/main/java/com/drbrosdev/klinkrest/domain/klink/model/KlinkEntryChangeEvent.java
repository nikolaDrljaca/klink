package com.drbrosdev.klinkrest.domain.klink.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class KlinkEntryChangeEvent {

    Operation operation;
    Row row;

    @Data
    @Builder(toBuilder = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {
        String id;
        String value;

        @JsonProperty("klink_id")
        String klinkId;
    }
}
