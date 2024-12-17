package com.drbrosdev.klinkrest.domain;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;

import java.util.UUID;

public interface KlinkDomainService {

    KlinkDto createKlink(KlinkDto klink);

    KlinkDto getKlink(UUID klinkId);

    void deleteKlink(UUID klinkId);

}
