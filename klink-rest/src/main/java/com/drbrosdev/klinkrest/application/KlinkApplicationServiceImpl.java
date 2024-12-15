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
        // if writeKey is not passed in, compare readKeys and only send down read key
        Predicate<KlinkDto> readKeyAccess = (value) -> value.getReadKey().equals(readKey);
        if (writeKey == null) {
            if (Boolean.FALSE.equals(readKeyAccess.test(klink))) {
                log.info(
                        "Access keys did not match for stored: {} and requested: {}",
                        klink.getReadKey(),
                        readKey);
                throw new IllegalArgumentException("Access keys did not match.");
            }
            return KlinkDto.builder()
                    .id(klink.getId())
                    .name(klink.getName())
                    .description(klink.getDescription())
                    .readKey(klink.getReadKey())
                    // do not send down the write key
                    .writeKey(null)
                    .entries(klink.getEntries())
                    .build();
        }
        // if write key is present, compare both keys and send down
        Predicate<KlinkDto> writeKeyAccess = (value) -> value.getWriteKey().equals(writeKey);
        if (Boolean.FALSE.equals(readKeyAccess.and(writeKeyAccess).test(klink))) {
            log.info(
                    "Access keys did not match for stored: ({} | {}) and requested: ({} | {})",
                    klink.getReadKey(),
                    readKey,
                    klink.getWriteKey(),
                    writeKey);
            throw new IllegalArgumentException("Access keys did not match.");
        }
        return klink;
    }
}
