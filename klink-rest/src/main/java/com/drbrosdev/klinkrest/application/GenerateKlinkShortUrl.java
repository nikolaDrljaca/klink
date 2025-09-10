package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkShortUrl;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import com.drbrosdev.klinkrest.gateway.hop.CreateHopPayload;
import com.drbrosdev.klinkrest.gateway.hop.HopService;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@UseCase
@Log4j2
@AllArgsConstructor
public class GenerateKlinkShortUrl {

    @Value("${spring.application.name:}")
    private String appBasePath;

    private final HopService hopService;
    private final KlinkDomainService klinkDomainService;

    private final ValidateKlinkAccess validateKlinkAccess;

    public String execute(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        /*
        1 Look for existing shortUrl in db
        if found and non-null: unwrap and based on access level return appropriate short url (from db entry)
        if NOT found:
            create a share link (look at logic on the client)
            pass it to hop service
            on success store into db and return result of service call
            on failure log and return share link (the not short one obv)
         */
        var inputKey = createKey(readKey, writeKey);
        var klink = klinkDomainService.getKlink(klinkId, inputKey);
        var accessLevel = requireNonNull(validateKlinkAccess.execute(
                klink.getKey(),
                inputKey));
        var existingShortUrl = klinkDomainService.getShortUrl(klinkId);
        if (existingShortUrl.isPresent()) {
            return unwrapShortUrl(existingShortUrl.get(), accessLevel);
        }
        var shareUrl = createShareUrl(
                klink,
                accessLevel);
        try {
            var hop = hopService.createHop(shareUrl)
                    .execute()
                    .body();
            // TODO: review functionality and support upsert here
            klinkDomainService.createShortUrl(
                    klinkId,
                    KlinkShortUrl.builder()
                            // TODO: here set full access or readOnly based on accessLevel
                            .build());
            return requireNonNull(hop)
                    .getUrl();
        } catch (Exception e) {
            log.warn("Could not reach hop-service. Failed to create short url.");
            log.info("Falling back to share url");
            return shareUrl;
        }
    }

    private static String createShareUrl(
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
