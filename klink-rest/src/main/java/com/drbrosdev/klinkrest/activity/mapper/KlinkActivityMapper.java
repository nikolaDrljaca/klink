package com.drbrosdev.klinkrest.activity.mapper;

import com.drbrosdev.klinkrest.domain.klink.dto.QueryExistingKlinkDto;
import com.drbrosdev.klinkrest.domain.klink.dto.QueryExistingKlinkItemDto;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.framework.OptionalMapperUtils;
import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkEntryApiDto;
import org.openapitools.model.QueryExistingPayloadApiDto;
import org.openapitools.model.QueryExistingPayloadKlinksInnerApiDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = OptionalMapperUtils.class)
public interface KlinkActivityMapper {

    @Mapping(target = "readKey", source = "key.readKey")
    @Mapping(target = "writeKey", source = "key.writeKey")
    KlinkApiDto mapTo(final Klink klink);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entries", ignore = true)
    Klink mapTo(
            final UUID id,
            final KlinkKey key,
            final String name,
            final @Nullable String description);

    @Mapping(target = "createdAt", ignore = true)
    KlinkEntry mapTo(KlinkEntryApiDto entry);

    KlinkKey mapTo(
            final String readKey,
            @Nullable final String writeKey);

    List<KlinkEntry> mapToEntries(final List<KlinkEntryApiDto> entries);

    QueryExistingKlinkItemDto mapTo(final QueryExistingPayloadKlinksInnerApiDto inner);

    List<QueryExistingKlinkItemDto> mapToKlinkItems(final List<QueryExistingPayloadKlinksInnerApiDto> inner);

    QueryExistingKlinkDto mapTo(final QueryExistingPayloadApiDto payload);

    default Long mapTo(final LocalDateTime date) {
        var zoneId = ZoneId.systemDefault();
        return date.atZone(zoneId).toEpochSecond();
    }
}
