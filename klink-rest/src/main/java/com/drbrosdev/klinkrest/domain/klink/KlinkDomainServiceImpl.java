package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.dto.QueryExistingKlinkItemDto;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkChangeEvent;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkShortUrl;
import com.drbrosdev.klinkrest.domain.klink.model.Operation;
import com.drbrosdev.klinkrest.domain.klink.usecase.GenerateKlinkKey;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkShortUrlEntity;
import com.drbrosdev.klinkrest.persistence.repository.KlinkEntryRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkKeyRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkRichEntryRepository;
import com.drbrosdev.klinkrest.persistence.repository.KlinkShortUrlRepository;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Log4j2
@Service
@RequiredArgsConstructor
public class KlinkDomainServiceImpl implements KlinkDomainService {

    private final GenerateKlinkKey generateKlinkKey;
    private final ValidateKlinkAccess validateKlinkAccess;
    private final EnrichKlinkEntryGateway enrichKlinkEntryGateway;

    private final KlinkRepository klinkRepository;
    private final KlinkEntryRepository klinkEntryRepository;
    private final KlinkRichEntryRepository richEntryRepository;
    private final KlinkKeyRepository klinkKeyRepository;
    private final KlinkShortUrlRepository klinkShortUrlRepository;

    private final KlinkDomainServiceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public KlinkKey getKeys(UUID klinkId) {
        return klinkKeyRepository.findByKlinkId(klinkId)
                .map(mapper::mapTo)
                .orElseThrow(() -> new EntityNotFoundException("KlinkKeys not found for Klink id: " + klinkId));
    }

    @Override
    @Transactional
    public Klink createKlink(
            UUID klinkId,
            String name,
            @Nullable String description,
            List<KlinkEntry> entries) {
        // create domain klink
        var klink = Klink.builder()
                .id(klinkId)
                .name(name)
                .description(description)
                .entries(entries)
                // assign keys
                .key(generateKlinkKey.execute())
                .build();
        // create and persist klink entity
        var klinkEntity = klinkRepository.save(createKlinkEntity(klink));
        // create and persist entries
        var storedEntries = klinkEntryRepository.saveAll(createKlinkEntryEntity(klink));
        // hand off entries for enrichment
        storedEntries.forEach(it -> enrichKlinkEntryGateway.submit(mapper.enrichJob(it)));
        // create and persist keys
        var keys = klinkKeyRepository.save(createKeyEntity(klink));
        // map to domain model and return
        return mapper.mapTo(
                klinkEntity,
                storedEntries,
                keys);
    }

    @Override
    @Transactional(readOnly = true)
    public Klink getKlink(
            UUID klinkId,
            KlinkKey inputKeys) {
        // retrieve klink
        var klink = retrieveKlink(klinkId);
        // validate access
        var access = validateKlinkAccess.execute(
                klink.getKey(),
                inputKeys);
        return switch (access) {
            // hide the write-key when read-only access is valid
            case READ_ONLY -> klink.toBuilder()
                    .key(klink.getKey()
                            .toBuilder()
                            .writeKey(null)
                            .build())
                    .build();

            case READ_WRITE -> klink;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Klink getKlink(UUID klinkId) {
        return retrieveKlink(klinkId);
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<KlinkEntry> getEntries(UUID klinkId) {
        return retrieveEntriesForKlink(klinkId);
    }

    @Override
    @Transactional(readOnly = true)
    public KlinkChangeEvent createKlinkChangeEvent(UUID klinkId) {
        var out = retrieveEntriesForKlink(klinkId)
                .sorted(comparing(KlinkEntry::getCreatedAt))
                .toList();
        // there are current entries - return out
        if (isNotEmpty(out)) {
            return KlinkChangeEvent.builder()
                    .operation(Operation.UPDATED)
                    .entries(out)
                    .build();
        }
        // check if the klink is deleted
        var klink = klinkRepository.findById(klinkId);
        if (klink.isPresent()) {
            // klink is still present but entries are empty
            return KlinkChangeEvent.builder()
                    .operation(Operation.UPDATED)
                    .entries(out) // will be empty
                    .build();
        }
        // default case - no klink and no entries
        return KlinkChangeEvent.builder()
                .operation(Operation.DELETED)
                .entries(emptyList())
                .build();
    }

    @Override
    @Transactional
    public void deleteKlink(
            UUID klinkId,
            KlinkKey key) {
        var storedKeys = retrieveKey(klinkId);
        var access = validateKlinkAccess.execute(
                key,
                storedKeys);
        if (access != KlinkAccessLevel.READ_WRITE) {
            throw new IllegalArgumentException("Write access is needed to delete klink!");
        }
        klinkRepository.deleteById(klinkId);
    }

    @Override
    @Transactional
    public Klink updateKlink(Klink klink) {
        var klinkId = klink.getId();
        // retrieve klink entity
        var entity = klinkRepository.findById(klinkId)
                .orElseThrow();
        var storedKey = retrieveKey(klinkId);
        // validate write access
        var access = validateKlinkAccess.execute(
                storedKey,
                klink.getKey());
        if (access != KlinkAccessLevel.READ_WRITE) {
            throw new IllegalArgumentException("Write access is needed to delete klink!");
        }
        // update necessary fields
        updateKlink(
                entity,
                klink);
        // map and return
        return retrieveKlink(klinkId);
    }

    @Override
    @Transactional
    public Stream<KlinkEntry> createKlinkEntries(
            UUID klinkId,
            KlinkKey key,
            List<KlinkEntry> entries) {
        var storedKey = retrieveKey(klinkId);
        var access = validateKlinkAccess.execute(
                storedKey,
                key);
        if (access != KlinkAccessLevel.READ_WRITE) {
            throw new IllegalArgumentException("Write access is needed to create klink entries!");
        }
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
        var created = klinkEntryRepository.saveAll(entities)
                .stream()
                .map(mapper::mapToEntry)
                .toList();
        // submit enrich jobs
        for (var klinkEntry : created) {
            enrichKlinkEntryGateway.submit(mapper.enrichJob(
                    klinkId,
                    klinkEntry));
        }
        // map and return
        return created.stream();
    }

    @Override
    @Transactional
    public void deleteKlinkEntries(
            UUID klinkId,
            KlinkKey key,
            List<KlinkEntry> entries) {
        var storedKey = retrieveKey(klinkId);
        var access = validateKlinkAccess.execute(
                storedKey,
                key);
        if (access != KlinkAccessLevel.READ_WRITE) {
            throw new IllegalArgumentException("Write access is needed to delete klinks!");
        }
        var toDelete = entries.stream()
                .map(KlinkEntry::getValue)
                .collect(toSet());
        var existingKlinks = klinkEntryRepository.findByKlinkId(klinkId)
                .stream()
                .filter(it -> toDelete.contains(it.getValue()))
                .toList();
        klinkEntryRepository.deleteAllInBatch(existingKlinks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Klink> queryExistingKlinks(List<QueryExistingKlinkItemDto> query) {
        var queryLookup = query.stream()
                .collect(Collectors.toMap(
                        QueryExistingKlinkItemDto::getId,
                        identity()));
        var klinkIds = queryLookup.keySet()
                .stream()
                .toList();
        // TODO: N+1 - can be optimized by utilizing a specific query
        return klinkRepository.findByIdIn(klinkIds)
                .map(it -> {
                    var keys = klinkKeyRepository.findByKlinkId(it.getId())
                            .orElseThrow();
                    return mapper.mapTo(
                            it,
                            // NOTE: entries are not necessary here
                            emptyList(),
                            keys);
                })
                .filter(it -> ofNullable(queryLookup.get(it.getId()))
                        .map(QueryExistingKlinkItemDto::getReadKey)
                        .map(KlinkKey::readOnly)
                        .map(readKey -> validateKlinkAccess.validate(
                                it.getKey(),
                                readKey))
                        // nulls are no permission
                        .map(access -> Objects.equals(access, KlinkAccessLevel.READ_ONLY))
                        .orElse(false))
                .toList();
    }

    @Override
    @Transactional
    public void deleteKlinksIn(List<UUID> klinkIds) {
        klinkRepository.deleteAllByIdIn(klinkIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Stream<Klink> getKlinks() {
        return klinkRepository.findAll()
                .stream()
                .map(it -> {
                    var entries = klinkEntryRepository.findByKlinkId(it.getId());
                    var keys = klinkKeyRepository.findByKlinkId(it.getId())
                            .orElseThrow(() -> new EntityNotFoundException("KlinkKeys not found for Klink id: " + it.getId()));
                    return mapper.mapTo(
                            it,
                            entries,
                            keys);
                });
    }

    @Override
    public Optional<KlinkShortUrl> getShortUrl(UUID klinkId) {
        return klinkShortUrlRepository.findByKlinkId(klinkId)
                .map(mapper::mapTo);
    }

    @Override
    public KlinkShortUrl createShortUrl(
            UUID klinkId,
            KlinkShortUrl shortUrl) {
        var existing = klinkShortUrlRepository.findByKlinkId(klinkId)
                .orElse(null);
        if (existing == null) {
            // create new and store
            var entity = KlinkShortUrlEntity.builder()
                    .id(randomUUID())
                    .fullAccessUrl(shortUrl.getFullAccessUrl())
                    .readOnlyUrl(shortUrl.getReadOnlyUrl())
                    .klinkId(klinkId)
                    .createdAt(now())
                    .build();
            return mapper.mapTo(klinkShortUrlRepository.save(entity));
        }
        // update existing
        existing.setFullAccessUrl(shortUrl.getFullAccessUrl());
        existing.setReadOnlyUrl(shortUrl.getReadOnlyUrl());
        return mapper.mapTo(klinkShortUrlRepository.save(existing));
    }

    protected KlinkKey retrieveKey(UUID klinkId) {
        return klinkKeyRepository.findByKlinkId(klinkId)
                .map(it -> KlinkKey.builder()
                        .readKey(it.getReadKey())
                        .writeKey(it.getWriteKey())
                        .build())
                .orElseThrow(() -> {
                    log.error("No keys found for klinkId {}", klinkId);
                    return new IllegalArgumentException("No keys found!");
                });
    }

    protected Stream<KlinkEntry> retrieveEntriesForKlink(UUID klinkId) {
        return klinkEntryRepository.findByKlinkId(klinkId)
                .stream()
                .map(entry ->
                        richEntryRepository.findByKlinkEntryId(entry.getId())
                                .map(it -> mapper.mapTo(entry, it))
                                .orElse(mapper.mapToEntry(entry)));
    }

    private Klink retrieveKlink(UUID klinkId) {
        var resultSet = klinkRepository.findKlinkById(klinkId);
        if (resultSet.isEmpty()) {
            throw new EntityNotFoundException("Klink not found for ID: " + klinkId);
        }
        var first = resultSet.get(0);
        var key = KlinkKey.builder()
                .readKey(first.getReadKey())
                .writeKey(first.getWriteKey())
                .build();
        var entries = resultSet.stream()
                .map(it -> KlinkEntry.builder()
                        .id(it.getKlinkEntryId())
                        .value(it.getKlinkEntry())
                        .description(it.getKlinkEntryDescription())
                        .title(it.getKlinkEntryTitle())
                        .createdAt(it.getEntryCreatedAt())
                        .build())
                .toList();
        var updatedAt = entries.stream()
                .map(KlinkEntry::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(first.getKlinkModifiedAt());
        return Klink.builder()
                .id(first.getKlinkId())
                .name(first.getKlinkName())
                .description(first.getKlinkDescription())
                .key(key)
                .entries(entries)
                .updatedAt(updatedAt)
                .build();
    }

    private void updateKlink(
            KlinkEntity entity,
            Klink klink) {
        entity.setName(klink.getName());
        entity.setDescription(klink.getDescription());
        entity.setModifiedAt(now());
    }

    private KlinkKeyEntity createKeyEntity(Klink klink) {
        return KlinkKeyEntity.builder()
                .klinkId(klink.getId())
                .readKey(klink.getKey().getReadKey())
                .writeKey(klink.getKey().getWriteKey())
                .build();
    }

    private KlinkEntity createKlinkEntity(Klink klink) {
        return KlinkEntity.builder()
                .id(klink.getId())
                .name(klink.getName())
                .description(klink.getDescription())
                .createdAt(now())
                .modifiedAt(now())
                .build();
    }

    private List<KlinkEntryEntity> createKlinkEntryEntity(Klink klink) {
        return klink.getEntries()
                .stream()
                .map(it -> createKlinkEntryEntity(
                        klink.getId(),
                        it))
                .collect(toList());
    }

    private KlinkEntryEntity createKlinkEntryEntity(
            UUID klinkId,
            KlinkEntry entry) {
        return KlinkEntryEntity.builder()
                .klinkId(klinkId)
                .value(entry.getValue())
                .createdAt(now())
                .build();
    }
}
