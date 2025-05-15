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
    Optional<WaypointEntity> findByRouteIdAndType(@NotBlank Long routeId, @NotBlank String type);

    long countByRouteIdAndType(@NotBlank Long routeId, @NotBlank String type);

    List<WaypointEntity> findAllPointsByRouteId(@NotBlank Long routeId);

    List<WaypointEntity> findAllPointsByRouteIdAndType(@NotBlank Long routeId, @NotBlank String type);

    Optional<WaypointEntity> findByRouteIdAndIndex(@NotBlank Long routeId, @NotBlank Long index);

    Optional<WaypointEntity> findById(@NotBlank Long id);

    boolean existsById(@NotBlank Long id);
}