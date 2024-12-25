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
@Table(name = "klink")
public class KlinkEntity {

    @Id
    private UUID id;

    String name;

    @Nullable
    String description;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "modified_at")
    LocalDateTime modifiedAt;
}
