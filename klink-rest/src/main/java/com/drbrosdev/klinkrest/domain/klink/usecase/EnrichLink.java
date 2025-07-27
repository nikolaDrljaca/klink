package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.utils.UseCase;
import lombok.extern.log4j.Log4j2;

@UseCase
@Log4j2
public class EnrichLink {
    // should handle exceptions
    // call the http client
    // pass result to parser
    // pass parser result to db
    // notify triggers
    public void execute(EnrichLinkJob job) {
        try {
            // TODO impl
            Thread.sleep(3_000);
        } catch (Exception e) {
            log.error("Failed to enrich {}.", job.getValue());
        }
    }
}
