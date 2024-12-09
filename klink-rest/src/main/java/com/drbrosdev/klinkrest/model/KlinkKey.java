package com.drbrosdev.klinkrest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "klink_key")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class KlinkKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    @Column(name = "read_key", length = 8)
    private String readKey;
    @Column(name = "write_key", length = 8)
    private String writeKey;
    @Column(name = "klink_id", nullable = false)
    private UUID klinkId;
}
