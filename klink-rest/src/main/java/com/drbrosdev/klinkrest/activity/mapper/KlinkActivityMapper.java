package com.drbrosdev.klinkrest.activity.mapper;

import com.drbrosdev.klinkrest.application.dto.QueryExistingKlinkDto;
import com.drbrosdev.klinkrest.application.dto.QueryExistingKlinkItemDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
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

    KlinkApiDto mapTo(final KlinkDto klinkDto);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entries", ignore = true)
    KlinkDto mapTo(
            final UUID id,
            final String readKey,
            final @Nullable String writeKey,
            final String name,
            final @Nullable String description);

    KlinkEntryDto mapTo(KlinkEntryApiDto entry);

    List<KlinkEntryDto> mapToEntries(final List<KlinkEntryApiDto> entries);

    QueryExistingKlinkItemDto mapTo(final QueryExistingPayloadKlinksInnerApiDto inner);

    List<QueryExistingKlinkItemDto> mapToKlinkItems(final List<QueryExistingPayloadKlinksInnerApiDto> inner);

    QueryExistingKlinkDto mapTo(final QueryExistingPayloadApiDto payload);

    default Long mapTo(final LocalDateTime date) {
        var zoneId = ZoneId.systemDefault();
        return date.atZone(zoneId).toEpochSecond();
    }
}
