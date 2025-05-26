package com.sep.backend.auth.login;

import com.sep.backend.entity.OtpVerificationTokenEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationTokenRepository extends JpaRepository<OtpVerificationTokenEntity, Long> {


    boolean existsByEmailIgnoreCase(@Email @NotBlank String email);

    void deleteByEmailIgnoreCase(@Email @NotBlank String email);

    Optional<OtpVerificationTokenEntity> findByEmailIgnoreCase(@Email @NotBlank String email);
}
