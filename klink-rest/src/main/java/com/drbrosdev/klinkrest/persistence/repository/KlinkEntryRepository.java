package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KlinkEntryRepository extends JpaRepository<KlinkEntryEntity, UUID> {
}
