package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkShortUrl;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import com.drbrosdev.klinkrest.gateway.hop.HopService;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@UseCase
@Log4j2
@RequiredArgsConstructor
public class GenerateKlinkShortUrl {

    @Value("${app.base-path:}")
    private String appBasePath;

    private final HopService hopService;
    private final KlinkDomainService klinkDomainService;

    private final ValidateKlinkAccess validateKlinkAccess;

    public String execute(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        var inputKey = createKey(readKey, writeKey);
        var klink = klinkDomainService.getKlink(klinkId);
        var accessLevel = requireNonNull(validateKlinkAccess.execute(
                klink.getKey(),
                inputKey));
        var existingShortUrl = klinkDomainService.getShortUrl(klinkId)
                .map(it -> unwrapShortUrl(it, accessLevel));
        if (existingShortUrl.isPresent()) {
            log.info(
                    "Found existing short url for {} {}",
                    klink.getName(),
                    accessLevel);
            return existingShortUrl.get();
        }

        var shareUrl = createShareUrl(
                klink,
                accessLevel);

        try {
            var hop = requireNonNull(hopService.createHop(shareUrl)
                    .execute()
                    .body());
            var shortUrl = KlinkShortUrl.builder();
            switch (accessLevel) {
                case READ_ONLY -> shortUrl.readOnlyUrl(hop.getUrl());
                case READ_WRITE -> shortUrl.fullAccessUrl(hop.getUrl());
            }

            klinkDomainService.createShortUrl(
                    klinkId,
                    shortUrl.build());

            return hop.getUrl();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            log.warn("Could not reach hop-service. Failed to create short url. Falling back to share url.");
            return shareUrl;
        }
    }

    private String createShareUrl(
            Klink klink,
            KlinkAccessLevel accessLevel) {
        var encoder = Base64.getEncoder()
                .withoutPadding();
        var keys = switch (accessLevel) {
            case READ_ONLY -> klink.getKey().getReadKey();
            case READ_WRITE -> klink.getKey().getReadKey() + klink.getKey().getWriteKey();
        };
        return new StringBuilder()
                .append(appBasePath)
                .append("/c/")
                .append(klink.getId())
                .append("/i?q=")
                .append(encoder.encodeToString(keys.getBytes()))
                .toString();
    }

    private static String unwrapShortUrl(
            KlinkShortUrl shortUrl,
            KlinkAccessLevel accessLevel) {
        return switch (accessLevel) {
            case READ_ONLY -> shortUrl.getReadOnlyUrl();
            case READ_WRITE -> shortUrl.getFullAccessUrl();
        };
    }

    private static KlinkKey createKey(String readKey, @Nullable String writeKey) {
        if (writeKey == null) {
            return KlinkKey.readOnly(readKey);
        }
        return KlinkKey.builder()
                .readKey(readKey)
                .writeKey(writeKey)
                .build();
    }
}
