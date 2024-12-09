package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KlinkRepository extends JpaRepository<KlinkEntity, UUID> {
}
