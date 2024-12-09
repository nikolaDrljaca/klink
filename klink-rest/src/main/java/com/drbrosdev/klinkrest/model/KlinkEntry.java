package com.drbrosdev.klinkrest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "klink_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class KlinkEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;
    @Column(name = "klink_id", nullable = false)
    private UUID klinkId;
    @Column(name = "value", nullable = false, length = 250)
    private String value;

}
