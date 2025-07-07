package com.drbrosdev.klinkrest.domain.klink;

import com.zaxxer.hikari.util.DriverDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;

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
    CommandLineRunner startNotifier(
            NotifierService service,
            NotificationHandler handler) {
        return (args) -> {
            var listener = service.createKlinkEntryChangeHandler(handler);
            var thread = new Thread(listener, "entry-change-listener");
            thread.start();
        };
    }
}
