package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KlinkKeyRepository extends JpaRepository<KlinkKeyEntity, UUID> {

    Optional<KlinkKeyEntity> findByKlinkId(UUID klinkId);
    boolean existsByKlinkId(UUID uuid);
    void deleteByKlinkId(UUID uuid);
}
