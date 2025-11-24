package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.enrich.EnrichKlinkEntryEvent;
import com.drbrosdev.klinkrest.utils.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

@Log4j2
@UseCase
@RequiredArgsConstructor
public class CreateKlinkEntries {

    private final KlinkDomainService klinkDomainService;

    private final ApplicationEventPublisher eventPublisher;

    public void execute(
            UUID klinkId,
            KlinkKey key,
            List<KlinkEntry> entries) {
        var created = klinkDomainService.createKlinkEntries(
                klinkId,
                key,
                entries)
                .toList();
        for (KlinkEntry entry : created) {
            eventPublisher.publishEvent(EnrichKlinkEntryEvent.builder()
                    .klinkId(klinkId)
                    .klinkEntryId(entry.getId())
                    .value(entry.getValue())
                    .build());
        }
    }
}
