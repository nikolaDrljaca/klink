package com.drbrosdev.klinkrest.repository;

import com.drbrosdev.klinkrest.model.KlinkKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KlinkKeyRepository extends JpaRepository<KlinkKey, UUID> {

    Optional<KlinkKey> findByKlinkId(UUID id);
}
