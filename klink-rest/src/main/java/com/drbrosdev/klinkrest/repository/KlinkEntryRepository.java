package com.drbrosdev.klinkrest.repository;

import com.drbrosdev.klinkrest.model.KlinkEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KlinkEntryRepository extends JpaRepository<KlinkEntry, UUID> {

    List<KlinkEntry> findAllByKlinkId(UUID id);
}
