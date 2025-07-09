package com.drbrosdev.klinkrest.framework.notifier;

import com.drbrosdev.klinkrest.domain.klink.KlinkNotifierService;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntryChangeNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Log4j2
public class KlinkNotifierServiceImpl implements KlinkNotifierService {

    private static final String KLINK_ENTRY_CHANGE = "klink_entry_change";

    private final JdbcTemplate template;

    private final ObjectMapper objectMapper;

    public KlinkNotifierServiceImpl(JdbcTemplate template) {
        this.template = template;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Runnable createKlinkEntryChangeHandler(Consumer<KlinkEntryChangeNotification> consumer) {
        return () -> template.execute((Connection c) -> {
            c.createStatement().execute("LISTEN " + KLINK_ENTRY_CHANGE);
            PGConnection conn = c.unwrap(PGConnection.class);
            while (!Thread.currentThread().isInterrupted()) {
                var notifications = conn.getNotifications(10_000);
                if (notifications == null) {
                    continue;
                }
                for (var value : notifications) {
                    createEntryChangeNotification(value)
                            .ifPresent(consumer);
                }
            }
            return 0;
        });
    }

    private Optional<KlinkEntryChangeNotification> createEntryChangeNotification(PGNotification notification) {
        var data = notification.getParameter();
        try {
            return ofNullable(objectMapper.readValue(
                    data,
                    new TypeReference<>() {}));
        } catch (JsonProcessingException e) {
            log.error(e);
            return empty();
        }
    }
}
