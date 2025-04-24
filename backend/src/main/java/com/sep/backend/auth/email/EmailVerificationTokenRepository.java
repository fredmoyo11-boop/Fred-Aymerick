package com.sep.backend.auth.email;

import com.sep.backend.entity.EmailVerificationTokenEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {

    boolean existsByEmail(@Email @NotBlank String email);

    void deleteByEmail(@Email @NotBlank String email);
    
    Optional<EmailVerificationTokenEntity> findByToken(@NotBlank String token);
}
