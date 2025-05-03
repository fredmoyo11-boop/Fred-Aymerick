package com.sep.backend.route;

import com.sep.backend.entity.WaypointEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaypointRepository extends JpaRepository<WaypointEntity, Long> {
    Optional<WaypointRepository> findById(@NotBlank long id);

    boolean existsById(@NotBlank long id);
}