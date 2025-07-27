package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.RichKlinkEntryPreview;
import com.drbrosdev.klinkrest.domain.klink.usecase.GenerateUrlPreview;
import com.drbrosdev.klinkrest.utils.UseCase;
import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UseCase
@Log4j2
public class JsoupGenerateUrlPreview implements GenerateUrlPreview {

    @Override
    public Optional<RichKlinkEntryPreview> execute(KlinkEntry entry) {
        try {
            // validate entry is URL
            if (!validUrl(entry)) {
                return Optional.empty();
            }
            // load document from URL
            var document = Jsoup.connect(entry.getValue())
                    .get();
            return Optional.of(RichKlinkEntryPreview.builder()
                    .title(parseTitle(document))
                    .description(parseDescription(document))
                    .build());
        } catch (Exception e) {
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
