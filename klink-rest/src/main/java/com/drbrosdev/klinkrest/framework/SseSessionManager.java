package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
@Log4j2
public class SseSessionManager {

    private final Map<UUID, List<SseEmitter>> sessions = new ConcurrentHashMap<>();

    private final JsonMapper mapper = (JsonMapper) new JsonMapper()
            .registerModule(new JavaTimeModule());

    public SseEmitter createSession(UUID klinkId) {
        BiConsumer<UUID, SseEmitter> remove = (id, emitter) -> {
            var emitters = sessions.get(klinkId);
            if (emitters == null) {
                return;
            }
            emitters.remove(emitter);
        };

        // create new emitter
        var emitter = new SseEmitter(0L); // no timeout - sendEvent will remove dead emitters
        // add to existing session or create new
        sessions.computeIfAbsent(klinkId, (id) -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> {
            log.info("SSE emitter {} completed.", klinkId);
            remove.accept(klinkId, emitter);
        });
        emitter.onTimeout(() -> {
            log.warn("SSE emitter {} closed due to timeout.", klinkId);
            remove.accept(klinkId, emitter);
        });
        emitter.onError((e) -> {
            log.error("SSE emitter {} closed due to error", klinkId, e);
            remove.accept(klinkId, emitter);
        });

        log.info("Created new session for klinkId: {}", klinkId);
        log.info("Session count for {} is {}", klinkId, sessions.get(klinkId).size());

        return emitter;
    }

    public void sendEvent(
            UUID klinkId,
            List<KlinkEntry> entries) {
        // extract emitters for klinkId session
        var current = sessions.get(klinkId);
        if (current == null) {
            return;
        }

        var deadEmitters = new ArrayList<SseEmitter>();
        for (var curr : current) {
            try {
                curr.send(
                        mapper.writeValueAsString(entries),
                        MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                curr.completeWithError(e);
                deadEmitters.add(curr);
            }
        }
        if (isNotEmpty(deadEmitters)) {
            log.info("Cleaning up {} dead emitters for {}", deadEmitters.size(), klinkId);
            current.removeAll(deadEmitters);
        }
    }
}
