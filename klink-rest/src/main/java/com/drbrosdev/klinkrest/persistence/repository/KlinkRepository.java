package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import com.drbrosdev.klinkrest.persistence.entity.KlinkFullRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface KlinkRepository extends JpaRepository<KlinkEntity, UUID> {

    Stream<KlinkEntity> findByIdIn(List<UUID> ids);

    void deleteAllByIdIn(List<UUID> ids);

    @Query(value = """
        select
            k.id as klinkId,
            k.name as klinkName,
            k.description as klinkDescription,
            k.created_at as klinkCreatedAt,
            k.modified_at as klinkModifiedAt,
            kk.read_key as readKey,
            kk.write_key as writeKey,
            ke.id as klinkEntryId,
            ke.value as klinkEntry,
            ke.created_at entryCreatedAt,
            rld.title as klinkEntryTitle,
            rld.description as klinkEntryDescription
        from klink k
                 left join klink_key kk on kk.klink_id = k.id
                 left join klink_entry ke on ke.klink_id = k.id
                 left join rich_link_data rld on ke.id = rld.klink_entry_id
        where k.id = :klinkId
    """, nativeQuery = true)
    List<KlinkFullRow> findKlinkById(@Param("klinkId") UUID klinkId);
}
