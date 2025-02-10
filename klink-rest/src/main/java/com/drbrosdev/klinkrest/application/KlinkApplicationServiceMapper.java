package com.drbrosdev.klinkrest.application;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.framework.OptionalMapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = OptionalMapperUtils.class)
public interface KlinkApplicationServiceMapper {

    @Mapping(target = "writeKey", ignore = true)
    KlinkDto mapToReadOnly(final KlinkDto klink);
}
