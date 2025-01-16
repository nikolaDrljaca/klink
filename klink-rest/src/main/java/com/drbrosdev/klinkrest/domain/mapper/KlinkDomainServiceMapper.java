package com.drbrosdev.klinkrest.domain.mapper;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KlinkDomainServiceMapper {

    @Mapping(target = "readKey", source = "key.readKey")
    @Mapping(target = "writeKey", source = "key.writeKey")
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "updatedAt", source = "entity.modifiedAt")
    KlinkDto mapTo(
            final KlinkEntity entity,
            final List<KlinkEntryEntity> entries,
            final KlinkKeyEntity key);

    @Mapping(target = "value", source = "value")
    KlinkEntryDto mapTo(final KlinkEntryEntity value);

}
