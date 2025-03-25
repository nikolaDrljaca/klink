package com.drbrosdev.klinkrest.persistence.repository;

import com.drbrosdev.klinkrest.persistence.entity.KlinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface KlinkRepository extends JpaRepository<KlinkEntity, UUID> {

    Stream<KlinkEntity> findByIdIn(List<UUID> ids);

    @Query("SELECT k FROM KlinkEntity k WHERE k.createdAt < :period")
    Stream<KlinkEntity> findAllOlderThenPeriod(LocalDateTime period);

    @Modifying
    @Query("DELETE FROM KlinkEntity k WHERE k.id IN :ids")
    void deleteAllByIds(@Param("ids") List<UUID> ids);
}
