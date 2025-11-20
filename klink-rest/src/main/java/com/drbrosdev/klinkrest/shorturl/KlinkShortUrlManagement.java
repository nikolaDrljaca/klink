package com.drbrosdev.klinkrest.shorturl;

import jakarta.annotation.Nullable;
import org.openapitools.api.ShortUrlApi;
import org.openapitools.model.KlinkShortUrlApiDto;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class KlinkShortUrlManagement implements ShortUrlApi {

    @Override
    public ResponseEntity<KlinkShortUrlApiDto> getKlinkShortUrl(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        return null;
    }
}
