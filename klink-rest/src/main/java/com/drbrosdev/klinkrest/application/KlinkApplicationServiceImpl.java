package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.isNull;

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
        var readAccess = validateReadAccess(
                klink,
                readKey);
        // no read access is granted
        if (Boolean.FALSE.equals(readAccess)) {
            log.info(
                    "Access keys did not match for stored: {} and requested: {}",
                    klink.getReadKey(),
                    readKey);
            throw new IllegalArgumentException("Access keys are not matching");
        }
        if (isNull(writeKey)) {
            return KlinkDto.builder()
                    .id(klink.getId())
                    .name(klink.getName())
                    .description(klink.getDescription())
                    .readKey(klink.getReadKey())
                    .writeKey(null) // Send writeKey only if provided
                    .entries(klink.getEntries())
                    .build();
        }
        var writeAccess = validateWriteAccess(
                klink,
                readKey,
                writeKey);
        // no write access is permitted
        if (Boolean.FALSE.equals(writeAccess)) {
            log.info(
                    "Access keys did not match for stored: ({} | {}) and requested: ({} | {})",
                    klink.getReadKey(),
                    readKey,
                    klink.getWriteKey(),
                    writeKey);
            throw new IllegalArgumentException("Access keys are not matching");
        }
        return KlinkDto.builder()
                .id(klink.getId())
                .name(klink.getName())
                .description(klink.getDescription())
                .readKey(klink.getReadKey())
                .writeKey(klink.getWriteKey())
                .entries(klink.getEntries())
                .build();
    }

    @Override
    public void deleteKlinkById(
            UUID klinkId,
            String readKey,
            String writeKey) {
        // fetch klink
        var klink = klinkDomainService.getKlink(klinkId);
        // validate read write access
        var writeAccess = validateWriteAccess(
                klink,
                readKey,
                writeKey);
        // no write access was granted
        if (Boolean.FALSE.equals(writeAccess)) {
            log.info(
                    "Access keys did not match for stored: ({} | {}) and requested: ({} | {})",
                    klink.getReadKey(),
                    readKey,
                    klink.getWriteKey(),
                    writeKey);
            throw new IllegalArgumentException("Access keys are not matching");
        }
        klinkDomainService.deleteKlink(klinkId);
    }

    private boolean validateReadAccess(
            KlinkDto klink,
            String readKey) {
        return klink.getReadKey().equals(readKey);
    }

    private boolean validateWriteAccess(
            KlinkDto klink,
            String readKey,
            String writeKey) {
        return klink.getWriteKey().equals(writeKey) &&
                validateReadAccess(
                        klink,
                        readKey);
    }
}
