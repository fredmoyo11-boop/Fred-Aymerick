package com.sep.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@Schema(description = "Represents an entity with soft-delete", allOf = AbstractEntity.class)
public class DurableEntity extends AbstractEntity {

    @Schema(description = "A flag marking the entity as deleted.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

}
