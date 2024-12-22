package com.drbrosdev.klinkrest.activity.mapper;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import com.drbrosdev.klinkrest.framework.OptionalMapperUtils;
import jakarta.annotation.Nullable;
import org.mapstruct.Mapper;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkEntryApiDto;

import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        uses = OptionalMapperUtils.class)
public interface KlinkActivityMapper {

    KlinkApiDto mapTo(final KlinkDto klinkDto);

    KlinkDto mapTo(
            final UUID id,
            final String readKey,
            final @Nullable String writeKey,
            final String name,
            final @Nullable String description);

    KlinkEntryDto mapTo(KlinkEntryApiDto entry);

    List<KlinkEntryDto> mapToEntries(final List<KlinkEntryApiDto> entries);

}
