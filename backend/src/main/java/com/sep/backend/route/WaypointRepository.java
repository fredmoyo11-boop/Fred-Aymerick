package com.sep.backend.route;

import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.route.WaypointType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface WaypointRepository extends JpaRepository<WaypointEntity, Long> {
    Optional<WaypointEntity> findByIdAndType(@NotBlank long routeId, @NotBlank String type);

    long countByIdAndType(@NotBlank long routeId, @NotBlank String type);

    List<WaypointEntity> findAllPointsById(@NotBlank long routeId);

    List<WaypointEntity> findAllPointsByIdAndType(@NotBlank long routeId, @NotBlank String type);

    Optional<WaypointEntity> findById(@NotBlank long id);

    boolean existsById(@NotBlank long id);
}