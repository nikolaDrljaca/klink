package com.drbrosdev.klinkrest.activity.mapper;

import com.drbrosdev.klinkrest.application.dto.CreateKlinkPayloadDto;
import com.drbrosdev.klinkrest.application.dto.KlinkDto;
import org.mapstruct.Mapper;
import org.openapitools.model.CreateKlinkPayloadApiDto;
import org.openapitools.model.KlinkApiDto;

@Mapper(componentModel = "spring")
public interface KlinkActivityMapper {

    CreateKlinkPayloadDto mapTo(final CreateKlinkPayloadApiDto createKlinkPayloadDto);

    KlinkApiDto mapTo(final KlinkDto klinkDto);

}
