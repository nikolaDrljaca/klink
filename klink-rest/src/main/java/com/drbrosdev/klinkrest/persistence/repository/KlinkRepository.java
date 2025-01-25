package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface KlinkRepository extends JpaRepository<KlinkEntity, UUID> {

    Stream<KlinkEntity> findByIdIn(List<UUID> ids);
}
