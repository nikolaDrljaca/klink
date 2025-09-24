package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkShortUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KlinkShortUrlRepository extends JpaRepository<KlinkShortUrlEntity, UUID> {

    Optional<KlinkShortUrlEntity> findByKlinkId(UUID klinkId);

}
