package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.RichKlinkEntryPreview;
import com.drbrosdev.klinkrest.persistence.entity.KlinkRichEntryEntity;
import com.drbrosdev.klinkrest.persistence.repository.KlinkRichEntryRepository;
import com.drbrosdev.klinkrest.utils.UseCase;
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
    // should handle exceptions
    // call the http client
    // pass result to parser
    // pass parser result to db
    // notify triggers

    private final GenerateUrlPreview generateUrlPreview;

    private final KlinkRichEntryRepository richEntryRepository;

    public void execute(EnrichLinkJob job) {
        try {
            // TODO impl
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
            // TODO trigger pg_notify for klink entries - jdbcTemplate call
            // TODO Remove log
            log.info("Generated link preview: {}", preview);
            // TODO Remove sleep
            Thread.sleep(2_000);
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

    private static KlinkEntry entry(EnrichLinkJob job) {
        return KlinkEntry.builder()
                .value(job.getValue())
                .build();
    }
}
