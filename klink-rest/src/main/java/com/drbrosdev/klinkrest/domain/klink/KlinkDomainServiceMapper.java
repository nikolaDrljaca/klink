package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
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

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "updatedAt", ignore = true)
    Klink mapTo(
            final KlinkEntity entity,
            final List<KlinkEntryEntity> entries,
            final KlinkKeyEntity key);

    KlinkKey mapTo(final KlinkKeyEntity key);

    @Mapping(target = "value", source = "value")
    KlinkEntry mapTo(final KlinkEntryEntity value);

    @Mapping(target = "value", source = "value")
    @Mapping(target = "klinkEntryId", source = "id")
    EnrichLinkJob enrichJob(final KlinkEntryEntity entry);

    @AfterMapping
    default void extractLatestCreatedAt(
            @MappingTarget Klink.KlinkBuilder builder,
            final KlinkEntity klink,
            final List<KlinkEntryEntity> entries) {
        var updatedAt = entries.stream()
                .map(KlinkEntryEntity::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(klink.getModifiedAt());
        builder.updatedAt(updatedAt);
    }
}

