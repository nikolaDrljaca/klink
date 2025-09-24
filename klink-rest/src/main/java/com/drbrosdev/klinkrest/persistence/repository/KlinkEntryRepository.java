package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KlinkEntryRepository extends JpaRepository<KlinkEntryEntity, UUID> {

    List<KlinkEntryEntity> findByKlinkId(UUID klinkId);

}
