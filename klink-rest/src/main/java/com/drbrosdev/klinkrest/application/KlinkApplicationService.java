package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface KlinkApplicationService {

    KlinkDto createKlink(
            UUID klinkId,
            String name,
            @Nullable String description,
            List<KlinkEntryDto> entries);

    KlinkDto getKlinkById(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey);

    void deleteKlinkById(
            UUID klinkId,
            String readKey,
            String writeKey);

}
