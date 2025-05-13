package com.sep.backend.route;

import com.sep.backend.entity.RouteEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    Optional<RouteEntity> findById(@NotBlank Long id);

    boolean existsById(@NotBlank Long id);
}