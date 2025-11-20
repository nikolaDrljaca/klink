package com.drbrosdev.klinkrest.domain.klink.model;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE) //Hides the constructor to force usage of the Builder.
public class KlinkKey {
    String readKey;
    String writeKey;

    public static KlinkKey readOnly(String readKey) {
        return KlinkKey.builder()
                .readKey(readKey)
                .writeKey(null)
                .build();
    }

    public static KlinkKey createKey(
            String readKey,
            @Nullable String writeKey) {
        if (writeKey == null) {
            return KlinkKey.readOnly(readKey);
        }
        return KlinkKey.builder()
                .readKey(readKey)
                .writeKey(writeKey)
                .build();
    }
}
