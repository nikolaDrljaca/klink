package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@UseCase
@RequiredArgsConstructor
public class ValidateKlinkAccess {

    public KlinkAccessLevel execute(
            KlinkKey keys,
            KlinkKey input) {
        if (validateWriteAccess(keys, input)) {
            return KlinkAccessLevel.READ_WRITE;
        }
        if (validateReadAccess(keys, input)) {
            return KlinkAccessLevel.READ_ONLY;
        }

        log.info(
                "Access keys did not match for stored: {}  and requested: {}",
                keys,
                input);
        throw new IllegalArgumentException("Access keys are not matching!");
    }

    @Nullable
    public KlinkAccessLevel validate(
            KlinkKey keys,
            KlinkKey input) {
        if (validateWriteAccess(keys, input)) {
            return KlinkAccessLevel.READ_WRITE;
        }
        if (validateReadAccess(keys, input)) {
            return KlinkAccessLevel.READ_ONLY;
        }
        return null;
    }

    private boolean validateReadAccess(
            KlinkKey klink,
            KlinkKey input) {
        return klink.getReadKey()
                .equals(input.getReadKey());
    }

    private boolean validateWriteAccess(
            KlinkKey klink,
            KlinkKey input) {
        return klink.getWriteKey().equals(input.getWriteKey()) &&
                validateReadAccess(
                        klink,
                        input);
    }
}
