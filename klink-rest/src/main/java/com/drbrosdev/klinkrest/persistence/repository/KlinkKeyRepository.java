package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KlinkKeyRepository extends JpaRepository<KlinkKeyEntity, UUID> {

    Optional<KlinkKeyEntity> findByKlinkId(UUID klinkId);
}
