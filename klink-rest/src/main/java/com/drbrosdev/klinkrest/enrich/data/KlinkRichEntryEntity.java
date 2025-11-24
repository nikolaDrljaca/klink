package com.drbrosdev.klinkrest.enrich.data;

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
@Table(name = "rich_link_data")
public class KlinkRichEntryEntity {

    @Id
    UUID id;

    @Column(name = "klink_entry_id")
    UUID klinkEntryId;

    @Nullable
    String title;

    @Nullable
    String description;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}
