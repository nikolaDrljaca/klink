package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import jakarta.annotation.Nullable;

import java.util.UUID;

public interface KlinkApplicationService {

    KlinkDto getKlinkById(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey);

    void deleteKlinkById(
            UUID klinkId,
            String readKey,
            String writeKey);

}
