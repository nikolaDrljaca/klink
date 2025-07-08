package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.framework.SseSessionManager;
import com.zaxxer.hikari.util.DriverDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;
import java.util.UUID;

@Configuration
public class NotifierConfiguration {

    /*
    Notifier should use a separate database connection from the hikari pool.
     */
    @Bean
    NotifierService notifierService(DataSourceProperties props) {
        var dataSource = new DriverDataSource(
                props.determineUrl(),
                props.determineDriverClassName(),
                new Properties(),
                props.determineUsername(),
                props.determinePassword());
        return new NotifierService(new JdbcTemplate(dataSource));
    }

    @Bean
    CommandLineRunner scaffoldKlinkEntryChangeEventFlow(
            NotifierService notifierService,
            KlinkDomainService klinkDomainService,
            SseSessionManager sessionManager) {
        return (args) -> {
            var listener = notifierService.createKlinkEntryChangeHandler(notification -> {
                var klinkId = UUID.fromString(notification.getRow()
                        .getKlinkId());
                var entries = klinkDomainService.getEntries(klinkId)
                        .toList();
                sessionManager.sendEvent(
                        klinkId,
                        entries);
            });
            var thread = new Thread(listener, "klink-entry-change-listener");
            thread.start();
        };
    }
}
