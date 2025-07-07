package com.drbrosdev.klinkrest.domain.klink.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Operation {
    @JsonProperty("deleted")
    DELETED("deleted"),

    @JsonProperty("inserted")
    INSERTED("inserted"),

    @JsonProperty("updated")
    UPDATED("updated");

    private final String value;

    public String value() {
        return value;
    }

    Operation(String value) {
        this.value = value;
    }
}
