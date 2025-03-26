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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Log4j2
public class KlinkDomainServiceImpl implements KlinkDomainService {

    private final KlinkRepository klinkRepository;

    private final KlinkEntryRepository klinkEntryRepository;

    private final KlinkKeyRepository klinkKeyRepository;

    private final KlinkDomainServiceMapper mapper;

    @Override
    @Transactional
    public KlinkDto createKlink(KlinkDto klink) {
        // check if klink ID already exists in database
        klinkRepository.findById(klink.getId())
                .ifPresent(entity -> {
                    log.warn(
                            "Supplied klink UUID {} already exists in the database!",
                            klink.getId());
                    throw new IllegalArgumentException("Could not create Klink");
                });
        // create and persist klink entity
        var klinkEntity = klinkRepository.save(createKlinkEntity(klink));
        // create and persist entries
        var entries = klinkEntryRepository.saveAll(createKlinkEntryEntity(klink));
        // create and persist keys
        var keys = klinkKeyRepository.save(createKeyEntity(klink));
        // map to domain model and return
        return mapper.mapTo(
                klinkEntity,
                entries,
                keys);
    }

    @Override
    @Transactional(readOnly = true)
    public KlinkDto getKlink(UUID klinkId) {
        return retrieveKlink(klinkId);
    }

    @Override
    @Transactional
    public void deleteKlink(UUID klinkId) {
        klinkRepository.deleteById(klinkId);
    }

    @Override
    @Transactional
    public KlinkDto updateKlink(
            UUID klinkId,
            KlinkDto klink) {
        // retrieve klink entity
        var entity = klinkRepository.findById(klinkId)
                .orElseThrow(() -> new EntityNotFoundException("Klink not found for ID: " + klinkId));
        // update necessary fields
        updateKlink(
                entity,
                klink);
        // map and return
        return retrieveKlink(klinkId);
    }

    @Override
    @Transactional
    public Stream<KlinkEntryDto> createKlinkEntries(
            UUID klinkId,
            List<KlinkEntryDto> entries) {
        // fetch existing klinks
        var existingKlinks = klinkEntryRepository.findByKlinkId(klinkId)
                .stream()
                .map(KlinkEntryEntity::getValue)
                .collect(toSet());
        // create new klinks
        var entities = entries.stream()
                // drop if already existing
                .filter(not(it -> existingKlinks.contains(it.getValue())))
                .map(it -> createKlinkEntryEntity(
                        klinkId,
                        it))
                .collect(toList());
        // map and return
        return klinkEntryRepository.saveAll(entities)
                .stream()
                .map(mapper::mapTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> queryExistingKlinks(List<UUID> klinkIds) {
        return klinkRepository.findByIdIn(klinkIds)
                .map(KlinkEntity::getId)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KlinkDto> retrieveKlinksIn(List<UUID> klinkIds) {
        return klinkRepository.findByIdIn(klinkIds)
                .map(it -> {
                    var entries = klinkEntryRepository.findByKlinkId(it.getId());
                    var keys = klinkKeyRepository.findByKlinkId(it.getId())
                            .orElseThrow();
                    return mapper.mapTo(
                            it,
                            entries,
                            keys);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<KlinkDto> retrieveKlinksOlderThenDays(int days) {
        LocalDateTime olderThan = now().minusDays(days);
        return klinkRepository.findAllOlderThenPeriod(olderThan)
                .map(it -> {
                    var entries = klinkEntryRepository.findByKlinkId(it.getId());
                    var keys = klinkKeyRepository.findByKlinkId(it.getId())
                            .orElseThrow();
                    return mapper.mapTo(
                            it,
                            entries,
                            keys);
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllKlinksOlderThenDays(List<UUID> klinkIds) {
        klinkRepository.deleteAllByIds(klinkIds);
    }

    private KlinkDto retrieveKlink(UUID klinkId) {
        var klink = klinkRepository.findById(klinkId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Klink not found for ID: " + klinkId));
        var klinkEntries = klinkEntryRepository.findByKlinkId(klinkId);
        var klinkKeys = klinkKeyRepository.findByKlinkId(klinkId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "KlinkKeys not found for Klink ID: " + klinkId));
        return mapper.mapTo(
                klink,
                klinkEntries,
                klinkKeys);
    }

    private void updateKlink(
            KlinkEntity entity,
            KlinkDto klink) {
        entity.setName(klink.getName());
        entity.setDescription(klink.getDescription());
        entity.setModifiedAt(now());
    }

    private KlinkKeyEntity createKeyEntity(KlinkDto klink) {
        return KlinkKeyEntity.builder()
                .klinkId(klink.getId())
                .readKey(klink.getReadKey())
                .writeKey(klink.getWriteKey())
                .build();
    }

    private KlinkEntity createKlinkEntity(KlinkDto klinkDto) {
        return KlinkEntity.builder()
                .id(klinkDto.getId())
                .name(klinkDto.getName())
                .description(klinkDto.getDescription())
                .createdAt(now())
                .modifiedAt(now())
                .build();
    }

    private List<KlinkEntryEntity> createKlinkEntryEntity(KlinkDto klinkDto) {
        return klinkDto.getEntries()
                .stream()
                .map(it -> createKlinkEntryEntity(
                        klinkDto.getId(),
                        it))
                .collect(toList());
    }

    private KlinkEntryEntity createKlinkEntryEntity(
            UUID klinkId,
            KlinkEntryDto entry) {
        return KlinkEntryEntity.builder()
                .klinkId(klinkId)
                .value(entry.getValue())
                .createdAt(now())
                .build();
    }
}
