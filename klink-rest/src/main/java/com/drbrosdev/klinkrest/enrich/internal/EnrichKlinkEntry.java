package com.drbrosdev.klinkrest.enrich.internal;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryChangeEvent;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryRichPreview;
import com.drbrosdev.klinkrest.domain.klink.model.Operation;
import com.drbrosdev.klinkrest.enrich.EnrichKlinkEntryEvent;
import com.drbrosdev.klinkrest.enrich.data.KlinkRichEntryRepository;
import com.drbrosdev.klinkrest.enrich.data.KlinkRichEntryEntity;
import com.drbrosdev.klinkrest.utils.UseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@UseCase
@Log4j2
@RequiredArgsConstructor
public class EnrichKlinkEntry {

    private final JsoupGenerateUrlPreview generateUrlPreview;

    private final KlinkRichEntryRepository richEntryRepository;

    private final ObjectMapper objectMapper;

    public void execute(EnrichKlinkEntryEvent job) {
        try {
            // parse rich link data
            var preview = generateUrlPreview.execute(entry(job))
                    .orElse(null);
            if (preview == null) {
                return;
            }
            // persist
            richEntryRepository.save(createRichEntry(
                    job.klinkEntryId(),
                    preview));
            // trigger notify change for entries
            notifyEntryChange(job)
                    .ifPresent(richEntryRepository::notifyEntryChanged);
        } catch (Exception e) {
            log.error("Failed to enrich {}.", job.value());
        }
    }

    private static KlinkRichEntryEntity createRichEntry(
            UUID klinkEntryId,
            KlinkEntryRichPreview preview) {
        return KlinkRichEntryEntity.builder()
                .id(randomUUID())
                .klinkEntryId(klinkEntryId)
                .title(preview.getTitle())
                .description(preview.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }

    protected Optional<String> notifyEntryChange(EnrichKlinkEntryEvent job) {
        var event = KlinkEntryChangeEvent.builder()
                .operation(Operation.UPDATED)
                .row(KlinkEntryChangeEvent.Row.builder()
                        .klinkId(job.klinkId().toString())
                        .value("")
                        .id("")
                        .build())
                .build();
        try {
            return Optional.of(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            // should never happen
            log.error("Failed to serialize KlinkEntryChangeEvet -- SHOULD NEVER HAPPEN!");
            return Optional.empty();
        }
    }

    private static KlinkEntry entry(EnrichKlinkEntryEvent job) {
        return KlinkEntry.builder()
                .value(job.value())
                .build();
    }
}
