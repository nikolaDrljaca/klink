package com.drbrosdev.klinkrest;

import org.apache.commons.lang3.NotImplementedException;
import org.openapitools.api.KlinkApi;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class KlinkController implements KlinkApi {

    @Override
    public ResponseEntity<Void> createKlink(CreateKlinkPayloadApiDto createKlinkPayloadApiDto) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public ResponseEntity<Void> deleteKlink(UUID klinkId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public ResponseEntity<KlinkApiDto> getKlink(UUID klinkId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public ResponseEntity<Void> syncKlink(
            UUID klinkId,
            KlinkApiDto klinkApiDto) {
        throw new NotImplementedException("TODO");
    }
}
