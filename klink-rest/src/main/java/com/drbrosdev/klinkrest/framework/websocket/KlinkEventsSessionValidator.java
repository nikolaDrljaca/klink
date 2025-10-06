package com.drbrosdev.klinkrest.framework.websocket;

import com.drbrosdev.klinkrest.domain.klink.KlinkDomainService;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkAccessLevel;
import com.drbrosdev.klinkrest.domain.klink.usecase.ValidateKlinkAccess;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static com.drbrosdev.klinkrest.domain.klink.model.KlinkKey.readOnly;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Log4j2
public class KlinkEventsSessionValidator implements HandshakeInterceptor {

    public static final String KLINK_ID_ATTR = "klinkId";
    public static final String READ_KEY_QUERY_PARAM = "read_key";

    private final ValidateKlinkAccess validateKlinkAccess;

    private final KlinkDomainService domainService;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        var details = parseKlinkSessionDetails(request.getURI());
        if (details == null) {
            // abort - no details
            return false;
        }
        var klinkId = details.getLeft();
        var readKey = details.getRight();

        var keys = domainService.getKeys(klinkId);
        var accessLevel = validateKlinkAccess.validate(
                keys,
                readOnly(readKey));
        if (accessLevel != KlinkAccessLevel.READ_ONLY) {
            // abort - does not have read privileges
            return false;
        }
        // set klinkId to websocketSession to access later
        attributes.put(
                KLINK_ID_ATTR,
                klinkId);
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

    @Nullable
    private static Pair<UUID, String> parseKlinkSessionDetails(@Nullable URI uri) {
        if (uri == null) {
            log.warn("Failed to parse URI into klink session details!");
            return null;
        }
        var queryParams = uri.getQuery(); // ?read_key={readKey}
        var path = uri.getPath(); // /events/klink/{klinkId}
        var components = path.split("/");
        if (components.length != 5) {
            log.warn("Failed to parse URI into klink session details! {} {}", path, components);
            return null;
        }
        var klinkId = UUID.fromString(components[components.length - 1]);
        var readKey = parseQueryParams(queryParams)
                .get(READ_KEY_QUERY_PARAM);
        if (readKey == null) {
            log.warn("Failed to parse read key from query params {}", queryParams);
            return null;
        }
        return Pair.of(klinkId, readKey);
    }

    private static Map<String, String> parseQueryParams(String value) {
        return stream(value.split("&"))
                .map(it -> it.split("=", 2))
                .filter(it -> it.length == 2)
                .collect(toMap(
                        it -> it[0],
                        it -> it[1]));
    }
}
