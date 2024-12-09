package com.drbrosdev.klinkrest.activity.mapper;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import com.drbrosdev.klinkrest.framework.OptionalMapperUtils;
import org.mapstruct.Mapper;
import org.openapitools.model.KlinkApiDto;
import org.openapitools.model.KlinkEntryApiDto;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = OptionalMapperUtils.class)
public interface KlinkActivityMapper {

    KlinkApiDto mapTo(final KlinkDto klinkDto);

    KlinkEntryDto mapTo(KlinkEntryApiDto entry);

    List<KlinkEntryDto> mapToEntries(final List<KlinkEntryApiDto> entries);

}
