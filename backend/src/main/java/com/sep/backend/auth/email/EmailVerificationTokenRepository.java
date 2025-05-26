package com.sep.backend.auth.email;

import com.sep.backend.entity.EmailVerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    void deleteByEmailIgnoreCase(String email);
    
    Optional<EmailVerificationTokenEntity> findByToken(String token);
}
