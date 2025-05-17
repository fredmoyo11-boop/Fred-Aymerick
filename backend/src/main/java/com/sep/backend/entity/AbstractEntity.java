package com.sep.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@MappedSuperclass
@Schema(description = "Represents an abstract entity.")
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "The id of the entity.", requiredMode = RequiredMode.REQUIRED)
    private Long id;


    @Column(name = "creation_time")
    @Schema(description = "The creation time of the entity.", requiredMode = RequiredMode.REQUIRED)
    private Long creationTime;

    @Column(name = "modification_time")
    @Schema(description = "The modification time of the entity.", requiredMode = RequiredMode.REQUIRED)
    private Long modificationTime;

    @PrePersist
    protected void onPersist() {
        creationTime = System.currentTimeMillis();
        modificationTime = creationTime;
    }

    @PreUpdate
    protected void onUpdate() {
        modificationTime = System.currentTimeMillis();
    }
}
