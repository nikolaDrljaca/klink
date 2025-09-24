package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkRichEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KlinkRichEntryRepository extends JpaRepository<KlinkRichEntryEntity, UUID> {

    Optional<KlinkRichEntryEntity> findByKlinkEntryId(UUID klinkEntryId);

    @Query(
            value = "SELECT pg_notify('klink_entry_change', :json_string)",
            nativeQuery = true)
    void notifyEntryChanged(@Param("json_string") String changeEvent);
}
