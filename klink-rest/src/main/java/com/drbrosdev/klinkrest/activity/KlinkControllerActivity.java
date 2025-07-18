package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.activity.mapper.KlinkActivityMapper;
import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.openapitools.api.KlinkApi;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkEntryApiDto;
import org.openapitools.model.KlinkSyncStatusApiDto;
import org.openapitools.model.PatchKlinkPayloadApiDto;
import org.openapitools.model.QueryExistingPayloadApiDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        return ok(mapper.mapTo(
                klinkDomainService.createKlink(
                        createKlinkPayloadApiDto.getId(),
                        createKlinkPayloadApiDto.getName(),
                        createKlinkPayloadApiDto.getDescription(),
                        mapper.mapToEntries(createKlinkPayloadApiDto.getEntries()))));
    }

    @Override
    public ResponseEntity<Void> createKlinkEntry(
            UUID klinkId,
            String readKey,
            String writeKey,
            List<KlinkEntryApiDto> klinkEntryApiDto) {
        log.info(
                "createKlinkEntry called with klinkId: {}, readKey: {}, writeKey: {} and payload: {}",
                klinkId,
                readKey,
                writeKey,
                klinkEntryApiDto);
        klinkDomainService.createKlinkEntries(
                klinkId,
                mapper.mapTo(readKey, writeKey),
                mapper.mapToEntries(klinkEntryApiDto));
        return ok().build();
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
        var klink = klinkDomainService.getKlink(
                klinkId,
                mapper.mapTo(readKey, writeKey));
        return ok(mapper.mapTo(klink));
    }

    @Override
    public ResponseEntity<List<KlinkApiDto>> queryExisting(QueryExistingPayloadApiDto klinkIds) {
        log.info(
                "queryExisting called with ids: {}",
                klinkIds);
        var input = mapper.mapTo(klinkIds);
        return ok(klinkDomainService.queryExistingKlinks(input.getKlinks())
                .stream()
                .map(mapper::mapTo)
                .toList());
    }

    @Override
    public ResponseEntity<Void> deleteKlink(
            UUID klinkId,
            String readKey,
            String writeKey) {
        log.info(
                "deleteKlink called with klinkId: {}, readKey: {}, writeKey: {}",
                klinkId,
                readKey,
                writeKey);
        klinkDomainService.deleteKlink(
                klinkId,
                mapper.mapTo(readKey, writeKey));
        return ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteKlinkEntries(
            UUID klinkId,
            String readKey,
            String writeKey,
            List<KlinkEntryApiDto> klinkEntryApiDto) {
        log.info(
                "deleteKlinkEntries called with klinkId: {}, readKey: {}, writeKey: {}",
                klinkId,
                readKey,
                writeKey);
        klinkDomainService.deleteKlinkEntries(
                klinkId,
                mapper.mapTo(readKey, writeKey),
                mapper.mapToEntries(klinkEntryApiDto));
        return ok().build();
    }

    @Override
    public ResponseEntity<KlinkSyncStatusApiDto> syncKlink(
            UUID klinkId,
            KlinkApiDto klinkApiDto) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public ResponseEntity<KlinkApiDto> updateKlink(
            UUID klinkId,
            String readKey,
            String writeKey,
            PatchKlinkPayloadApiDto patchKlinkPayloadApiDto) {
        log.info("updateKlink called with {} and {}", klinkId, patchKlinkPayloadApiDto);
        return ok(mapper.mapTo(klinkDomainService.updateKlink(
                mapper.mapTo(
                        klinkId,
                        mapper.mapTo(readKey, writeKey),
                        patchKlinkPayloadApiDto.getName(),
                        patchKlinkPayloadApiDto.getDescription()))));
    }
}
