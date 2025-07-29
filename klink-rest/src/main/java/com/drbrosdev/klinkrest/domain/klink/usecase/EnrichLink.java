package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryChangeEvent;
import com.drbrosdev.klinkrest.domain.klink.model.Operation;
import com.drbrosdev.klinkrest.domain.klink.model.RichKlinkEntryPreview;
import com.drbrosdev.klinkrest.persistence.entity.KlinkRichEntryEntity;
import com.drbrosdev.klinkrest.persistence.repository.KlinkRichEntryRepository;
import com.drbrosdev.klinkrest.utils.UseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@UseCase
@Log4j2
@RequiredArgsConstructor
public class EnrichLink {

    private final GenerateUrlPreview generateUrlPreview;

    private final KlinkRichEntryRepository richEntryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void execute(EnrichLinkJob job) {
        try {
            // parse rich link data
            var preview = generateUrlPreview.execute(entry(job))
                    .orElse(null);
            if (preview == null) {
                log.warn("Unable to generate rich link preview for {}", job.getValue());
                return;
            }
            // persist
            createRichEntry(
                    job.getKlinkEntryId(),
                    preview);
            // trigger notify change for entries
            notifyEntryChange(job);
        } catch (Exception e) {
            log.error("Failed to enrich {}.", job.getValue());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void createRichEntry(
            UUID klinkEntryId,
            RichKlinkEntryPreview preview) {
        var entry = KlinkRichEntryEntity.builder()
                .id(randomUUID())
                .klinkEntryId(klinkEntryId)
                .title(preview.getTitle())
                .description(preview.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        richEntryRepository.save(entry);
    }

    protected void notifyEntryChange(EnrichLinkJob job) {
        var event = KlinkEntryChangeEvent.builder()
                .operation(Operation.UPDATED)
                .row(KlinkEntryChangeEvent.Row.builder()
                        .klinkId(job.getKlinkId().toString())
                        .value("")
                        .id("")
                        .build())
                .build();
        try {
            var rawEvent = objectMapper.writeValueAsString(event);
            richEntryRepository.notifyEntryChanged(rawEvent);
        } catch (JsonProcessingException e) {
            // should never happen
            log.error("Failed to serialize KlinkEntryChangeEvet -- SHOULD NEVER HAPPEN!");
        }
    }

    private static KlinkEntry entry(EnrichLinkJob job) {
        return KlinkEntry.builder()
                .value(job.getValue())
                .build();
    }
}
