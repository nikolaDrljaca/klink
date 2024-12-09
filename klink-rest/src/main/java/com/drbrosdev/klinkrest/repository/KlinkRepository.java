package com.drbrosdev.klinkrest.repository;

import com.drbrosdev.klinkrest.model.Klink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KlinkRepository extends JpaRepository<Klink, UUID> {
}
