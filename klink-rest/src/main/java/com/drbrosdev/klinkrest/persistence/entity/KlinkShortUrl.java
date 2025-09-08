package com.drbrosdev.klinkrest.persistence.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "klink_short_url")
public class KlinkShortUrl {
    @Id
    UUID id;

    @Column(name = "klink_entry_id")
    UUID klinkEntryId;

    @Nullable
    String fullAccessUrl;

    @Nullable
    String readOnlyUrl;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
