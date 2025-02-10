package com.drbrosdev.klinkrest.domain;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface KlinkDomainService {

    KlinkDto createKlink(KlinkDto klink);

    KlinkDto getKlink(UUID klinkId);

    void deleteKlink(UUID klinkId);

    KlinkDto updateKlink(
            UUID klinkId,
            KlinkDto klink);

    Stream<KlinkEntryDto> createKlinkEntries(
            UUID klinkId,
            List<KlinkEntryDto> entries);

    List<UUID> queryExistingKlinks(List<UUID> klinkIds);

    List<KlinkDto> retrieveKlinksIn(List<UUID> klinkIds);
}
