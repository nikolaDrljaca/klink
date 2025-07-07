package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryChangeNotification;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Log4j2
public class NotificationHandler implements Consumer<KlinkEntryChangeNotification> {

    @Override
    public void accept(KlinkEntryChangeNotification change) {
        log.info("Consumed message: {}", change);
    }

    // depend on notifierService and domainService (whatever fetches klinks)
    // scaffold function with klinkId as param
    // function should fetch data and map it to what the client is expecting
}
