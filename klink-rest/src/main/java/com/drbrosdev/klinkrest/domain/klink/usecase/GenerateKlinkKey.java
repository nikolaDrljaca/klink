package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.utils.UseCase;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@UseCase
@NoArgsConstructor
public class GenerateKlinkKey {

    private static final Integer KEY_LENGTH = 8;

    public KlinkKey execute() {
        return KlinkKey.builder()
                .readKey(generateKey())
                .writeKey(generateKey())
                .build();
    }

    private static String generateKey() {
        // Keep non-static import
        return RandomStringUtils.secure()
                .nextAlphanumeric(KEY_LENGTH)
                .toUpperCase();
    }
}
