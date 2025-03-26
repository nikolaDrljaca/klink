package com.drbrosdev.klinkrest.scheduler;

import com.drbrosdev.klinkrest.domain.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiredKlinkService {

    private static final Logger LOG = LoggerFactory.getLogger( ExpiredKlinkService.class );

    private final KlinkDomainService klinkDomainService;

    @Value("${isDeleteScheduleTaskActive}")
    private boolean isDeleteScheduleTaskActive;

    @Value("${daysToKeepKlinks}")
    private int daysToKeepKlinks;

    public ExpiredKlinkService(KlinkDomainService klinkDomainService) {
        this.klinkDomainService = klinkDomainService;
    }

    @Scheduled(cron = "${schedulerTime}")
    public void deleteExpiredKlinks() {

        if(!isDeleteScheduleTaskActive) {
            return;
        }
        LOG.info( "Deleting expired klinks..." );
        var klinkList = klinkDomainService.retrieveKlinksOlderThenDays(daysToKeepKlinks)
                .stream()
                .map(KlinkDto::getId)
                .toList();
        if(klinkList.isEmpty()) {
            return;
        }
        LOG.info("Found {} expired klinks", klinkList.size());
        klinkDomainService.deleteAllKlinksOlderThenDays(klinkList);
        LOG.info("Deleted {} expired klinks", klinkList.size());
    }
}
