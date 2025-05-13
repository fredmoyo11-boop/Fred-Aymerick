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
    Optional<WaypointEntity> findByRouteIdAndType(@NotBlank long routeId, @NotBlank String type);

    long countByRouteIdAndType(@NotBlank long routeId, @NotBlank String type);

    List<WaypointEntity> findAllPointsByRouteId(@NotBlank long routeId);

    List<WaypointEntity> findAllPointsByRouteIdAndType(@NotBlank long routeId, @NotBlank String type);

    Optional<WaypointEntity> findByRouteIdAndIndex(@NotBlank long routeId, @NotBlank long index);

    Optional<WaypointEntity> findById(@NotBlank long id);

    boolean existsById(@NotBlank long id);
}