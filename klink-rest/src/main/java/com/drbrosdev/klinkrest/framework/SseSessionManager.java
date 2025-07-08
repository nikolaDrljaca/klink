package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

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
        var emitter = new SseEmitter(0L);
        // add to existing session or create new
        sessions.computeIfAbsent(klinkId, (id) -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remove.accept(klinkId, emitter));
        emitter.onTimeout(() -> {
            log.warn("SSE emitter closed due to timeout.");
            remove.accept(klinkId, emitter);
        });
        emitter.onError((e) -> {
            log.error("SSE emitter closed due to error", e);
            remove.accept(klinkId, emitter);
        });

        log.info("Connected new session for klinkId: {}", klinkId);

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
                curr.send(SseEmitter.event()
                        .name("klink-entry-change")
                        .data(mapper.writeValueAsString(entries))
                        .build());
            } catch (IOException e) {
                deadEmitters.add(curr);
            }
        }
        current.removeAll(deadEmitters);
    }
}
