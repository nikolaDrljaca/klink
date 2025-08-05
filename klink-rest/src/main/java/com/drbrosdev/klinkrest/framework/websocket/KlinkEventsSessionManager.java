package com.drbrosdev.klinkrest.framework.websocket;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkChangeEvent;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Service
@Log4j2
public class KlinkEventsSessionManager {

    private final Map<UUID, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    private final JsonMapper mapper = (JsonMapper) new JsonMapper()
            .registerModule(new JavaTimeModule());

    public void createSession(
            UUID klinkId,
            WebSocketSession incoming) {
        var existingSessions = sessions.computeIfAbsent(
                klinkId,
                (id) -> new CopyOnWriteArrayList<>());
        existingSessions.add(incoming);
        var count = sessions.get(klinkId).size();
        log.info(
                "Created new session for klinkId: {}. Count {}.",
                klinkId,
                count);
    }

    public void removeSession(
            UUID klinkId,
            WebSocketSession incoming) {
        var existingSessions = sessions.get(klinkId);
        if (isEmpty(existingSessions)) {
            return;
        }
        existingSessions.remove(incoming);
        var sessionCount = existingSessions.size();
        log.info(
                "Cleared session for {}. Left {}.",
                klinkId,
                sessionCount);
    }

    public void sendEvent(
            UUID klinkId,
            KlinkChangeEvent event) {
        // extract sessions
        var existingSessions = sessions.get(klinkId);
        if (isEmpty(existingSessions)) {
            return;
        }
        // send event to all sessions for klink
        for (var current : existingSessions) {
            try {
                var message = mapper.writeValueAsString(event);
                current.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error(
                        "Sending message to {} failed. Closing session.",
                        klinkId);
            }
        }
    }
}
