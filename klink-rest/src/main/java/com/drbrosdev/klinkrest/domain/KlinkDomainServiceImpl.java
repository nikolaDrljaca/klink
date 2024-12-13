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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

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
            UUID id,
            String name,
            List<KlinkEntryDto> entries) {
        var entryEntities = entries.stream()
                .map(it -> KlinkEntryEntity.builder()
                        .value(it.getValue())
                        .klinkId(id)
                        .build())
                .collect(toList());
        var klink = KlinkEntity.builder()
                .id(id)
                .name(name)
                // TODO: Extension to insert `description`
                .description("")
                .createdAt(now())
                .modifiedAt(now())
                .build();
        var key = KlinkKeyEntity.builder()
                .klinkId(id)
                .readKey(createKey())
                .writeKey(createKey())
                .build();
        // create klink
        var savedKlink = klinkRepository.save(klink);
        // create entries
        var savedEntries = klinkEntryRepository.saveAll(entryEntities);
        // create key
        var savedKeys = klinkKeyRepository.save(key);
        return mapper.mapTo(
                savedKlink,
                savedEntries,
                savedKeys);
    }

    @Override
    @Transactional
    public KlinkDto getKlink(UUID uuid){
        var klink = klinkRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Klink not found for ID: " + uuid));

        var klikEntries = klinkEntryRepository.findByKlinkId(uuid)
                .orElseThrow(() -> new EntityNotFoundException("KlikEntries not found for Klink ID: " + uuid));

        var klinkKeys = klinkKeyRepository.findByKlinkId(uuid)
                .orElseThrow(() -> new EntityNotFoundException("KlinkKeys not found for Klink ID: " + uuid));

        return mapper.mapTo(klink, klikEntries, klinkKeys);
    }


    private String createKey() {
        // Keep non-static import
        return RandomStringUtils.secure()
                .nextAlphanumeric(KEY_LENGTH)
                .toUpperCase();
    }
}
