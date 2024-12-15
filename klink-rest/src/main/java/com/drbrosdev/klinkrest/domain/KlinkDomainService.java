package com.drbrosdev.klinkrest.domain;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;

import java.util.List;
import java.util.UUID;

public interface KlinkDomainService {

    KlinkDto createKlink(
            UUID id,
            String name,
            List<KlinkEntryDto> entries);

    KlinkDto getKlink(UUID id);

    void deleteKlink(UUID id);

}
