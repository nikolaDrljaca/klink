package com.drbrosdev.klinkrest.controller;

import com.drbrosdev.klinkrest.service.KlinkService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.apache.commons.lang3.NotImplementedException;
import org.openapitools.api.KlinkApi;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkSyncStatusApiDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class KlinkController implements KlinkApi {

    private final KlinkService klinkService;

    public KlinkController(KlinkService klinkService) {
        this.klinkService = klinkService;
    }

    @Override
    @PostMapping("/klink")
    public ResponseEntity<KlinkApiDto> createKlink(@Parameter(required = true) @Valid
                                                   @RequestBody CreateKlinkPayloadApiDto createKlinkPayloadApiDto) {
        return klinkService.createKlink(createKlinkPayloadApiDto);
    }

    @Override
    @DeleteMapping("/klink/{klinkId}")
    public ResponseEntity<Void> deleteKlink(@PathVariable UUID klinkId) {
        return klinkService.deleteKlink(klinkId);
    }

    @Override
    @GetMapping("/klink/{klinkId}")
    public ResponseEntity<KlinkApiDto> getKlink(@PathVariable UUID klinkId) {
        return klinkService.getKlink(klinkId);
    }

    @Override
    public ResponseEntity<KlinkSyncStatusApiDto> syncKlink(
            UUID klinkId,
            KlinkApiDto klinkApiDto) {
        throw new NotImplementedException("TODO");
    }
}
