package com.drbrosdev.klinkrest.activity;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.framework.websocket.KlinkEventsSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

import static com.drbrosdev.klinkrest.framework.websocket.KlinkEventsSessionValidator.KLINK_ID_ATTR;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class KlinkEventHandler extends TextWebSocketHandler {

    private final KlinkEventsSessionManager sessionManager;

    private final KlinkDomainService klinkDomainService;

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) {
        // omit - do nothing when the client sends a message
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var klinkId = parseKlinkId(session);
        // create the session
        sessionManager.createSession(
                klinkId,
                session);
        // send down initial event
        var changeEvent = klinkDomainService.createKlinkChangeEvent(klinkId);
        sessionManager.sendEvent(
                klinkId,
                changeEvent);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {
        sessionManager.removeSession(
                parseKlinkId(session),
                session);
    }

    private static UUID parseKlinkId(WebSocketSession session) {
        return ofNullable(session.getAttributes().get(KLINK_ID_ATTR))
                .map(it -> (UUID) it)
                .orElseThrow();
    }
}
