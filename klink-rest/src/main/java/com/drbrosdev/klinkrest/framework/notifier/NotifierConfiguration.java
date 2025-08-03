package com.drbrosdev.klinkrest.framework.notifier;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.KlinkNotifierService;
import com.drbrosdev.klinkrest.framework.websocket.KlinkEventsSessionManager;
import org.postgresql.Driver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.UUID;

@Configuration
public class NotifierConfiguration {

    /*
    Notifier should use a separate database connection from the hikari pool.
     */
    @Bean
    KlinkNotifierService notifierService(DataSourceProperties props) {
        var dataSource = new SimpleDriverDataSource(
                new Driver(),
                props.determineUrl(),
                props.determineUsername(),
                props.determinePassword());
        return new KlinkNotifierServiceImpl(new JdbcTemplate(dataSource));
    }

    @Bean
    CommandLineRunner scaffoldKlinkEntryChangeEventFlow(
            KlinkNotifierService notifierService,
            KlinkDomainService klinkDomainService,
            KlinkEventsSessionManager sessionManager) {
        return (args) -> {
            var listener = notifierService.createKlinkEntryChangeHandler(notification -> {
                var klinkId = UUID.fromString(notification.getRow()
                        .getKlinkId());
                var entries = klinkDomainService.createKlinkChangeEvent(klinkId);
                sessionManager.sendEvent(
                        klinkId,
                        entries);
            });
            var thread = new Thread(listener, "klink-entry-change-listener-ws");
            thread.setDaemon(true);
            thread.start();
        };
    }
}
