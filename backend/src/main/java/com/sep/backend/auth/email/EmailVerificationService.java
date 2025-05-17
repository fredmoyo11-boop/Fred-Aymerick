package com.sep.backend.auth.email;

import com.sep.backend.ErrorMessages;
import com.sep.backend.account.AccountService;
import com.sep.backend.auth.registration.RegistrationException;
import com.sep.backend.entity.EmailVerificationTokenEntity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {
    private final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final AccountService accountService;
    private final EmailVerificationTokenRepository tokenRepository;


    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository, AccountService accountService) {
        this.tokenRepository = tokenRepository;
        this.accountService = accountService;
    }

    /**
     * Saves a new verification token for the email.
     *
     * @param email The email.
     * @return The verification token entity.
     */
    @Transactional
    public EmailVerificationTokenEntity save(String email) {
        // delete verification token if unverified token exists
        if (tokenRepository.existsByEmail(email)) {
            log.debug("Deleting email verification token for {}", email);
            tokenRepository.deleteByEmail(email);
            log.info("Deleted email verification token for {}", email);
            log.debug("Still exists? {}", tokenRepository.existsByEmail(email));
        }

        log.debug("Generating email verification token for {}", email);
        String verificationToken = UUID.randomUUID().toString();
        var emailVerificationTokenEntity = new EmailVerificationTokenEntity();
        emailVerificationTokenEntity.setEmail(email);
        emailVerificationTokenEntity.setToken(verificationToken);
        emailVerificationTokenEntity.setExpirationTime(LocalDateTime.now().plusHours(24));
        log.info("Generated email verification token for {}", email);

        log.debug("Saving verification token for {}", email);
        emailVerificationTokenEntity = tokenRepository.save(emailVerificationTokenEntity);
        log.info("Saved verification token for {}", email);

        return emailVerificationTokenEntity;
    }

    /**
     * Verifies a verification token.
     *
     * @param token The verification token to verify.
     * @return The email address associated to the token.
     * @throws RegistrationException when token doesn't exist or is expired.
     */
    public String verify(String token) throws RegistrationException {

        var emailVerificationTokenEntity = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RegistrationException(ErrorMessages.INVALID_VERIFICATION_TOKEN));
        log.debug("Found email verification token for {}", emailVerificationTokenEntity.getEmail());

        if (emailVerificationTokenEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            log.info("Email verification token expired for {}", emailVerificationTokenEntity.getEmail());
            throw new RegistrationException(ErrorMessages.INVALID_VERIFICATION_TOKEN);
        }

        String email = emailVerificationTokenEntity.getEmail();

        log.debug("Verifying {}", email);
        accountService.verifyAccount(email);
        log.info("Verified {}", email);

        return email;
    }
}
