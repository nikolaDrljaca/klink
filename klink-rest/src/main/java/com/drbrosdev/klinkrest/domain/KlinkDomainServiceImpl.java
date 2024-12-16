package com.drbrosdev.klinkrest.domain;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import com.drbrosdev.klinkrest.domain.mapper.KlinkDomainServiceMapper;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import com.drbrosdev.klinkrest.persistence.repository.KlinkEntryRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkKeyRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Log4j2
public class KlinkDomainServiceImpl implements KlinkDomainService {

    private final KlinkRepository klinkRepository;

    private final KlinkEntryRepository klinkEntryRepository;

    private final KlinkKeyRepository klinkKeyRepository;

    private final KlinkDomainServiceMapper mapper;

    private static final Integer KEY_LENGTH = 8;

    @Override
    @Transactional
    public KlinkDto createKlink(
            UUID klinkId,
            String name,
            List<KlinkEntryDto> entries) {
        var entryEntities = entries.stream()
                .map(it -> KlinkEntryEntity.builder()
                        .value(it.getValue())
                        .klinkId(klinkId)
                        .build())
                .collect(toList());
        var klink = KlinkEntity.builder()
                .id(klinkId)
                .name(name)
                // TODO: Extension to insert `description`
                .description("")
                .createdAt(now())
                .modifiedAt(now())
                .build();
        var key = KlinkKeyEntity.builder()
                .klinkId(klinkId)
                .readKey(createKey())
                .writeKey(createKey())
                .build();
        // create klink
        var savedKlink = klinkRepository.save(klink);
        // create entries
        var savedEntries = klinkEntryRepository.saveAll(entryEntities);
        // create key
        var savedKeys = klinkKeyRepository.save(key);
        // map to domain model
        return mapper.mapTo(
                savedKlink,
                savedEntries,
                savedKeys);
    }

    @Override
    @Transactional(readOnly = true)
    public KlinkDto getKlink(UUID klinkId){
        var klink = klinkRepository.findById(klinkId)
                .orElseThrow(() -> new EntityNotFoundException("Klink not found for ID: " + klinkId));
        var klinkEntries = klinkEntryRepository.findByKlinkId(klinkId);
        var klinkKeys = klinkKeyRepository.findByKlinkId(klinkId)
                .orElseThrow(() -> new EntityNotFoundException("KlinkKeys not found for Klink ID: " + klinkId));
        // map to domain model
        return mapper.mapTo(
                klink,
                klinkEntries,
                klinkKeys);
    }

    @Override
    @Transactional
    public void deleteKlink(UUID klinkId) {
        klinkRepository.deleteById(klinkId);
    }

    private String createKey() {
        // Keep non-static import
        return RandomStringUtils.secure()
                .nextAlphanumeric(KEY_LENGTH)
                .toUpperCase();
    }
}
