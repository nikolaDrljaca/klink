package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.activity.mapper.KlinkActivityMapper;
import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.openapitools.api.KlinkApi;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkSyncStatusApiDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.ResponseEntity.ok;

@Log4j2
@RestController
@RequiredArgsConstructor
public class KlinkControllerActivity implements KlinkApi {

    private final KlinkDomainService klinkDomainService;

    private final KlinkActivityMapper mapper;

    @Override
    public ResponseEntity<KlinkApiDto> createKlink(CreateKlinkPayloadApiDto createKlinkPayloadApiDto) {
        log.info(
                "createKlink called with payload: {}",
                createKlinkPayloadApiDto);
        var klink = klinkDomainService.createKlink(
                createKlinkPayloadApiDto.getId(),
                createKlinkPayloadApiDto.getName(),
                mapper.mapToEntries(createKlinkPayloadApiDto.getEntries()));
        return ok(mapper.mapTo(klink));
    }

    @Override
    public ResponseEntity<Void> deleteKlink(UUID klinkId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public ResponseEntity<KlinkApiDto> getKlink(UUID klinkId) {
        log.info(
                "getKlink called with klinkId: {}",
                klinkId);
        var klink = klinkDomainService.getKlink(klinkId);
        return ok(mapper.mapTo(klink));
    }

    @Override
    public ResponseEntity<KlinkSyncStatusApiDto> syncKlink(
            UUID klinkId,
            KlinkApiDto klinkApiDto) {
        throw new NotImplementedException("TODO");
    }
}
