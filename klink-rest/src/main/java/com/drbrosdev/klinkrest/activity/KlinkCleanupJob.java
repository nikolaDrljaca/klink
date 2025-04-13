package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.application.KlinkApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Log4j2
@Service
@RequiredArgsConstructor
public class KlinkCleanupJob {

    private final KlinkApplicationService klinkApplicationService;

    @Value("${cleanupTaskActive}")
    private boolean isDeleteScheduleTaskActive;

    @Scheduled(cron = "${cleanupSchedule}")
    public void deleteExpiredKlinks() {
        if (!isDeleteScheduleTaskActive) {
            log.info("KlinkCleanupJob is not active, skipping deletion of expired Klinks");
            return;
        }

        log.info("Executing expired klink cleanup job.");
        klinkApplicationService.executeKlinkCleanup();
    }
}
