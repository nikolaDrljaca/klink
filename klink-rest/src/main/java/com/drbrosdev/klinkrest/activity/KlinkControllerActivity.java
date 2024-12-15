package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.activity.mapper.KlinkActivityMapper;
import com.drbrosdev.klinkrest.application.KlinkApplicationService;
import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import jakarta.annotation.Nullable;
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

    private final KlinkApplicationService klinkApplicationService;

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
    public ResponseEntity<KlinkApiDto> getKlink(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        log.info(
                "getKlink called with klinkId: {}, readKey: {}, writeKey: {}",
                klinkId,
                readKey,
                writeKey);
        var klink = klinkApplicationService.getKlinkById(
                klinkId,
                readKey,
                writeKey);
        return ok(mapper.mapTo(klink));
    }

    @Override
    public ResponseEntity<Void> deleteKlink(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        log.info(
                "deleteKlink called with klinkId: {}, readKey: {}, writeKey: {}",
                klinkId,
                readKey,
                writeKey);

        klinkApplicationService.deleteKlinkById(
                klinkId,
                readKey,
                writeKey);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<KlinkSyncStatusApiDto> syncKlink(
            UUID klinkId,
            KlinkApiDto klinkApiDto) {
        throw new NotImplementedException("TODO");
    }
}
