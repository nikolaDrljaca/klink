package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class KlinkApplicationServiceImpl implements KlinkApplicationService {

    private final KlinkDomainService klinkDomainService;

    private static final Integer KEY_LENGTH = 8;

    @Override
    public KlinkDto createKlink(
            UUID klinkId,
            String name,
            @Nullable String description,
            List<KlinkEntryDto> entries) {
        // create domain klink
        var klink = KlinkDto.builder()
                .id(klinkId)
                .name(name)
                .description(description)
                .entries(entries)
                // assign keys
                .readKey(createKey())
                .writeKey(createKey())
                .build();
        return klinkDomainService.createKlink(klink);
    }

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
                    .updatedAt(klink.getUpdatedAt())
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
                .updatedAt(klink.getUpdatedAt())
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

    @Override
    public KlinkDto updateKlink(KlinkDto klink) {
        // retrieve stored klink
        var stored = klinkDomainService.getKlink(klink.getId());
        // validate write access
        if (Boolean.FALSE.equals(validateWriteAccess(
                klink,
                stored.getReadKey(),
                stored.getWriteKey()))) {
            throw new IllegalArgumentException("Access keys don't match!");
        }
        // update klink and return
        return klinkDomainService.updateKlink(
                klink.getId(),
                klink);
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

    private String createKey() {
        // Keep non-static import
        return RandomStringUtils.secure()
                .nextAlphanumeric(KEY_LENGTH)
                .toUpperCase();
    }
}
