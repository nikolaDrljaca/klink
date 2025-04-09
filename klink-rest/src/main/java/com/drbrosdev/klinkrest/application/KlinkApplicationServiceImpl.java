package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.application.dto.QueryExistingKlinkDto;
import com.drbrosdev.klinkrest.application.dto.QueryExistingKlinkItemDto;
import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Log4j2
@Service
@RequiredArgsConstructor
public class KlinkApplicationServiceImpl implements KlinkApplicationService {

    private final KlinkDomainService klinkDomainService;

    private final KlinkApplicationServiceMapper mapper;

    private static final Integer KEY_LENGTH = 8;

    @Value("${klinkExiprationDuration}")
    private int daysToKeepKlinks;

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
            return mapper.mapToReadOnly(klink);
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
        return klink;
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

    @Override
    public Stream<KlinkEntryDto> createKlinkEntries(
            UUID klinkId,
            String readKey,
            String writeKey,
            List<KlinkEntryDto> entries) {
        // fetch klink
        var klink = klinkDomainService.getKlink(klinkId);
        // check write access
        if (Boolean.FALSE.equals(validateWriteAccess(
                klink,
                readKey,
                writeKey))) {
            throw new IllegalArgumentException("Access keys don't match!");
        }
        // create entries and return
        return klinkDomainService.createKlinkEntries(
                klinkId,
                entries);
    }

    @Override
    public List<KlinkDto> queryExistingKlinks(QueryExistingKlinkDto query) {
        if (isEmpty(query.getKlinks())) {
            return emptyList();
        }
        var queryKlinks = query.getKlinks()
                .stream()
                .collect(toMap(
                        QueryExistingKlinkItemDto::getId,
                        identity()));
        // retrieve all klinks by using uuid
        var klinks = klinkDomainService.retrieveKlinksIn(queryKlinks.values()
                .stream()
                .map(QueryExistingKlinkItemDto::getId)
                .toList());
        // filter incoming list with read access
        return klinks.stream()
                .filter(it -> queryKlinks.containsKey(it.getId()))
                // validate read access for each
                .filter(it -> validateReadAccess(
                        it,
                        queryKlinks.get(it.getId()).getReadKey()))
                // return as read-only -- do not expose write key
                .map(mapper::mapToReadOnly)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteKlinksOlderThenDays() {
        var date = LocalDateTime.now().minusDays(daysToKeepKlinks);
        var klinks = klinkDomainService.retrieveKlinksOlderThenDays(date)
                .stream()
                .map(KlinkDto::getId)
                .toList();

        if(!klinks.isEmpty()) {
            klinkDomainService.deleteAllKlinksOlderThenDays(klinks);
        }
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
