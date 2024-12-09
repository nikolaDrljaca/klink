package com.drbrosdev.klinkrest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "klink")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Klink {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    // UUID provided by the client
    private UUID id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", length = 100)
    private String description;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

}
