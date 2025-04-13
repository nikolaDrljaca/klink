package com.drbrosdev.klinkrest.domain;

import com.drbrosdev.klinkrest.domain.dto.KlinkDto;
import com.drbrosdev.klinkrest.domain.dto.KlinkEntryDto;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface KlinkDomainServiceMapper {

    @Mapping(target = "readKey", source = "key.readKey")
    @Mapping(target = "writeKey", source = "key.writeKey")
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "updatedAt", ignore = true)
    KlinkDto mapTo(
            final KlinkEntity entity,
            final List<KlinkEntryEntity> entries,
            final KlinkKeyEntity key);

    @Mapping(target = "value", source = "value")
    KlinkEntryDto mapTo(final KlinkEntryEntity value);


    @AfterMapping
    default void extractLatestCreatedAt(
            @MappingTarget KlinkDto.KlinkDtoBuilder builder,
            final KlinkEntity klink,
            final List<KlinkEntryEntity> entries) {
        var updatedAt = entries.stream()
                .map(KlinkEntryEntity::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(klink.getModifiedAt());
        builder.updatedAt(updatedAt);
    }
}

