package com.drbrosdev.klinkrest.domain.klink;

import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.model.Klink;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkEntry;
import com.drbrosdev.klinkrest.domain.klink.model.KlinkKey;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkRichEntryEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "updatedAt", ignore = true)
    Klink klinkWithEntries(
            final KlinkEntity entity,
            final List<KlinkEntry> entries,
            final KlinkKeyEntity key);

    KlinkKey mapTo(final KlinkKeyEntity key);

    @Mapping(target = "id", source = "value.id")
    @Mapping(target = "value", source = "value.value")
    @Mapping(target = "createdAt", source = "value.createdAt")
    @Mapping(target = "title", source = "richEntry.title")
    @Mapping(target = "description", source = "richEntry.description")
    KlinkEntry mapTo(
            final KlinkEntryEntity value,
            final KlinkRichEntryEntity richEntry);

    @Mapping(target = "value", source = "value")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    KlinkEntry mapToEntry(final KlinkEntryEntity value);

    @Mapping(target = "value", source = "value")
    @Mapping(target = "klinkEntryId", source = "id")
    EnrichLinkJob enrichJob(final KlinkEntryEntity entry);

    @Mapping(target = "value", source = "entry.value")
    @Mapping(target = "klinkEntryId", source = "entry.id")
    @Mapping(target = "klinkId", source = "klinkId")
    EnrichLinkJob enrichJob(
            UUID klinkId,
            final KlinkEntry entry);

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

    @AfterMapping
    default void extractLatestCreatedAtWithEntries(
            @MappingTarget Klink.KlinkBuilder builder,
            final KlinkEntity klink,
            final List<KlinkEntry> entries) {
        var updatedAt = entries.stream()
                .map(KlinkEntry::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(klink.getModifiedAt());
        builder.updatedAt(updatedAt);
    }
}

