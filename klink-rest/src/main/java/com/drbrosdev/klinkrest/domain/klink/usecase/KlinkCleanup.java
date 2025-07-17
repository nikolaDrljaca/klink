package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.utils.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.function.Predicate.not;

@UseCase
@RequiredArgsConstructor
@Log4j2
public class KlinkCleanup {

    private final KlinkDomainService klinkDomainService;

    public void executeKlinkCleanup(int daysToKeepKlinks) {
        var date = now().minusDays(daysToKeepKlinks);
        var klinks = klinkDomainService.getKlinks()
                // find all klinks older than 30 days
                .filter(it -> isOlderThan(it, date))
                // filter out klinks where most recent entry is not older than 30 days
                .filter(not(it -> containsRecentEntries(it, date)))
                .map(Klink::getId)
                .toList();
        // no eligible klinks found
        if (klinks.isEmpty()) {
            log.info("No eligible Klinks found for cleanup.");
            return;
        }
        // delete eligible klinks
        log.info(
                "Cleaning up {} klinks.",
                klinks.size());
        klinkDomainService.deleteKlinksIn(klinks);
    }

    protected boolean isOlderThan(
            Klink klink,
            LocalDateTime date) {
        return klink.getUpdatedAt().isBefore(date);
    }

    protected boolean containsRecentEntries(
            Klink klink,
            LocalDateTime date) {
        return klink.getEntries()
                .stream()
                .anyMatch(it -> it.getCreatedAt().isAfter(date));
    }
}
