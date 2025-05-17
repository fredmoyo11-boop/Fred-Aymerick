package com.sep.backend.account;


import com.sep.backend.entity.DriverEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import jakarta.validation.constraints.NotNull;
@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long> {
    Optional<DriverEntity> findByEmail(@Email @NotBlank String email);

    Optional<DriverEntity> findByUsername(@NotBlank String username);

    boolean existsByEmail(@Email @NotBlank String email);

    boolean existsByUsername(@NotBlank String username);

    List<DriverEntity> findByUsernameContainingIgnoreCase(@NotNull String part);
}
