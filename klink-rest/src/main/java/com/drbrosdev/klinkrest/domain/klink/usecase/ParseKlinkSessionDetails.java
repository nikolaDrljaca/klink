package com.drbrosdev.klinkrest.domain.klink.usecase;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkSessionDetails;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@Log4j2
public class ParseKlinkSessionDetails {

    public static final String READ_KEY_QUERY_PARAM = "read_key";

    @Nullable
    public KlinkSessionDetails execute(@Nullable URI uri) {
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
        return KlinkSessionDetails.builder()
                .klinkId(klinkId)
                .readKey(readKey)
                .build();
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
