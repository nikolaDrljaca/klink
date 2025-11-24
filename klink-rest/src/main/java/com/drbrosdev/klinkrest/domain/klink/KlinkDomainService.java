package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.dto.QueryExistingKlinkItemDto;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkChangeEvent;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkShortUrl;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface KlinkDomainService {

    KlinkKey getKeys(UUID klinkId);

    Klink createKlink(
            UUID klinkId,
            String name,
            @Nullable String description,
            List<KlinkEntry> entries);

    Klink getKlink(
            UUID klinkId,
            KlinkKey inputKeys);

    Klink getKlink(UUID klinkId);

    KlinkChangeEvent createKlinkChangeEvent(UUID klinkId);

    void deleteKlink(
            UUID klinkId,
            KlinkKey key);

    void deleteKlinksIn(List<UUID> klinkIds);

    Klink updateKlink(Klink klink);

    Stream<KlinkEntry> createKlinkEntries(
            UUID klinkId,
            KlinkKey key,
            List<KlinkEntry> entries);

    void deleteKlinkEntries(
            UUID klinkId,
            KlinkKey key,
            List<KlinkEntry> entries);

    List<Klink> queryExistingKlinks(List<QueryExistingKlinkItemDto> query);

    Stream<Klink> getKlinks();

    Optional<KlinkShortUrl> getShortUrl(UUID klinkId);

    KlinkShortUrl createShortUrl(
            UUID klinkId,
            KlinkShortUrl shortUrl);
}
