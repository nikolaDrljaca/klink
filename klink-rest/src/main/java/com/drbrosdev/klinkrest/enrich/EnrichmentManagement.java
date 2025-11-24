package com.drbrosdev.klinkrest.enrich;

import com.drbrosdev.klinkrest.enrich.internal.EnrichKlinkEntryJobProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrichmentManagement {

    private final EnrichKlinkEntryJobProcessor jobProcessor;

    @ApplicationModuleListener
    void on(EnrichKlinkEntryEvent event) {
        jobProcessor.submit(event);
    }
}
