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
            // TODO
            log.info("Completed job {}", job.getValue());
        } catch (Exception e) {
            log.error("Job for {} failed.", job.getValue());
        }
    }
}
