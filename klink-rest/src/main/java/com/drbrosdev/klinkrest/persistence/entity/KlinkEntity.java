package com.drbrosdev.klinkrest.persistence.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    String readKey;

    String writeKey;

    @Nullable
    String description;

}
