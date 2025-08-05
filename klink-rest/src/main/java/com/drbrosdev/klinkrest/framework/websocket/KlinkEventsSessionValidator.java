package com.drbrosdev.klinkrest.framework.websocket;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.usecase.ParseKlinkSessionDetails;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.drbrosdev.klinkrest.domain.klink.model.KlinkKey.readOnly;

@RequiredArgsConstructor
public class KlinkEventsSessionValidator implements HandshakeInterceptor {

    public static final String KLINK_ID_ATTR = "klinkId";

    private final ValidateKlinkAccess validateKlinkAccess;
    private final ParseKlinkSessionDetails parseKlinkSessionDetails;

    private final KlinkDomainService domainService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        var details = parseKlinkSessionDetails.execute(request.getURI());
        if (details == null) {
            // abort - no details
            return false;
        }
        var keys = domainService.getKeys(details.getKlinkId());
        var accessLevel = validateKlinkAccess.validate(
                keys,
                readOnly(details.getReadKey()));
        if (accessLevel != KlinkAccessLevel.READ_ONLY) {
            // abort - does not have read privileges
            return false;
        }
        // set klinkId to websocketSession to access later
        attributes.put(
                KLINK_ID_ATTR,
                details.getKlinkId());
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no - op
    }
}
