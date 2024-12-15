package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@Service
@RequiredArgsConstructor
public class KlinkApplicationServiceImpl implements KlinkApplicationService {

    private final KlinkDomainService klinkDomainService;

    @Override
    public KlinkDto getKlinkById(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {
        // fetch klink
        var klink = klinkDomainService.getKlink(klinkId);

        validateKeys(klink, readKey, writeKey);

        return KlinkDto.builder()
                .id(klink.getId())
                .name(klink.getName())
                .description(klink.getDescription())
                .readKey(klink.getReadKey())
                .writeKey(writeKey == null ? null : klink.getWriteKey()) // Send writeKey only if provided
                .entries(klink.getEntries())
                .build();
    }

    @Override
    public void deleteKlinkById(
            UUID klinkId,
            String readKey,
            @Nullable String writeKey) {

        // fetch klink
        var klink = klinkDomainService.getKlink(klinkId);

        validateKeys(klink, readKey, writeKey);

        klinkDomainService.deleteKlink(klinkId);
    }

    /**
     * Validates keys from client and on server using readKey and optional writeKey.
     *
     * @param klink   The KlinkDto to validate against.
     * @param readKey The provided readKey.
     * @param writeKey The optional provided writeKey.
     */
    private void validateKeys(KlinkDto klink,
                                String readKey,
                                @Nullable String writeKey) {
        if (!klink.getReadKey().equals(readKey)) {
            log.info(
                    "Access keys did not match for stored: {} and requested: {}",
                    klink.getReadKey(), readKey);
            throw new IllegalArgumentException("Access keys did not match.");
        }

        if (writeKey != null && !klink.getWriteKey().equals(writeKey)) {
            log.info(
                    "Access keys did not match for stored: ({} | {}) and requested: ({} | {})",
                    klink.getReadKey(), klink.getWriteKey(), readKey, writeKey);
            throw new IllegalArgumentException("Access keys did not match.");
        }
    }
}
