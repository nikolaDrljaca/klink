package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.enrich.EnrichKlinkEntryEvent;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

@Log4j2
@UseCase
@RequiredArgsConstructor
public class CreateKlink {

    private final KlinkDomainService klinkDomainService;

    private final ApplicationEventPublisher eventPublisher;

    public Klink execute(
            UUID klinkId,
            String name,
            @Nullable String description,
            List<KlinkEntry> entries) {
        var klink = klinkDomainService.createKlink(
                klinkId,
                name,
                description,
                entries);
        for (var entry : klink.getEntries()) {
            eventPublisher.publishEvent(EnrichKlinkEntryEvent.builder()
                    .klinkId(klink.getId())
                    .klinkEntryId(entry.getId())
                    .value(entry.getValue())
                    .build());
        }
        return klink;
    }

}
