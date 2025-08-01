package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.RichKlinkEntryPreview;
import com.drbrosdev.klinkrest.domain.klink.usecase.GenerateUrlPreview;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UseCase
@Log4j2
public class JsoupGenerateUrlPreview implements GenerateUrlPreview {

    private static final String USER_AGENT = "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/601.2.4 (KHTML, like Gecko) Version/9.0.1 Safari/601.2.4 facebookexternalhit/1.1 Facebot Twitterbot/1.0";

    private final Connection session = Jsoup.newSession()
            .userAgent(USER_AGENT)
            .timeout(6000); // 6 minutes

    @Override
    public Optional<RichKlinkEntryPreview> execute(KlinkEntry entry) {
        try {
            // validate entry is URL
            if (!validUrl(entry)) {
                return Optional.empty();
            }
            // load document from URL
            var document = session.newRequest(entry.getValue())
                    .get();
            return Optional.of(RichKlinkEntryPreview.builder()
                    .title(parseTitle(document))
                    .description(parseDescription(document))
                    .build());
        } catch (Exception e) {
            log.warn("Jsoup parsing failure {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    @Nullable
    private static String parseTitle(Document document) {
        var ogTitle = parseProperty(
                document,
                "meta[property=\"og:title\"]");
        if (isNotBlank(ogTitle)) {
            return ogTitle;
        }
        var xTitle = parseProperty(
                document,
                "meta[name=\"twitter:title\"]");
        if (isNotBlank(xTitle)) {
            return xTitle;
        }
        var header1 = parseProperty(
                document,
                "h1");
        if (isNotBlank(header1)) {
            return header1;
        }
        var header2 = parseProperty(
                document,
                "h2");
        if (isNotBlank(header2)) {
            return header2;
        }
        return null;
    }

    @Nullable
    private static String parseDescription(Document document) {
        var ogDesc = parseProperty(
                document,
                "meta[property=\"og:description\"]");
        if (isNotBlank(ogDesc)) {
            return ogDesc;
        }
        var xDesc = parseProperty(
                document,
                "meta[name=\"twitter:description\"]");
        if (isNotBlank(xDesc)) {
            return xDesc;
        }
        var desc = parseProperty(
                document,
                "meta[property=\"description\"]");
        if (isNotBlank(desc)) {
            return desc;
        }
        return null;
    }

    private static String parseProperty(
            Document document,
            String property) {
        return ofNullable(document.selectFirst(property))
                .map(it -> it.attr("content"))
                .orElse("");
    }

    private static boolean validUrl(KlinkEntry entry) {
        try {
            new URL(entry.getValue());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
