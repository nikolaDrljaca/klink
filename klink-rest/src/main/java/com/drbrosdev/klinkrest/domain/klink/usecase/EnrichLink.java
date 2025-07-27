package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.utils.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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

    public void execute(EnrichLinkJob job) {
        try {
            // TODO impl
            // parse rich link data
            var preview = generateUrlPreview.execute(KlinkEntry.builder()
                            .value(job.getValue())
                            .build()) // TODO this stinks a bit
                    .orElse(null);
            if (preview == null) {
                log.warn("Unable to generate rich link preview for {}", job.getValue());
                return;
            }
            //TODO pass preview to repo for storage
            // TODO Remove log
            log.info("Generated link preview: {}", preview);
            // TODO Remove sleep
            Thread.sleep(2_000);
        } catch (Exception e) {
            log.error("Failed to enrich {}.", job.getValue());
        }
    }
}
