package com.sep.backend.account;

import com.sep.backend.entity.CustomerEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByEmail(@Email @NotBlank String email);

    Optional<CustomerEntity> findByUsername(@NotBlank String username);

    boolean existsByEmail(@Email @NotBlank String email);

    boolean existsByUsername(@NotBlank String username);

    List<CustomerEntity> findByUsernameContainingIgnoreCase(@NotNull String part);
}
