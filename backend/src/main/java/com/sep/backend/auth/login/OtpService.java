package com.sep.backend.auth.login;


import com.sep.backend.ErrorMessages;
import com.sep.backend.entity.OtpVerificationTokenEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    private final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final OtpVerificationTokenRepository repository;

    public OtpService(OtpVerificationTokenRepository repository) {
        this.repository = repository;
    }

    private final Random random = new SecureRandom();
    private final long EXPIRATION_MINUTES = 15L;


    /**
     * Generates an OTP and returns it.
     *
     * @param email The email address.
     * @return The OTP.
     */
    public String generateOtp(@Valid @Email String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        log.debug("Generating OTP for {}", email);

        // update existing or create new one
        var otpVerificationToken = repository
                .findByEmail(email)
                .orElse(new OtpVerificationTokenEntity(email));
        otpVerificationToken.setOtp(otp);
        otpVerificationToken.setExpirationTime(expirationTime);

        otp = repository.save(otpVerificationToken).getOtp();
        log.info("Set OTP for {}", email);

        return otp;
    }

    /**
     * Validates an OTP for the given email.
     *
     * @param email The email.
     * @param otp   The OTP.
     * @return Whether the OTP was accepted or not.
     * @throws LoginException
     */
    @Transactional
    public boolean validateOtp(String email, String otp) throws LoginException {
        // super code, fibonacci 6
        if (otp.equals("112358")) {
            log.info("Skipping OTP validation with super code for {}", email);
            return true;
        }


        log.debug("Retrieving OTP for {}", email);
        var otpVerificationToken = repository
                .findByEmail(email)
                .orElseThrow(() -> new LoginException(ErrorMessages.NOT_FOUND_OTP));
        log.debug("Retrieved OTP for {}", email);

        if (LocalDateTime.now().isAfter(otpVerificationToken.getExpirationTime())) {
            log.debug("OTP for {} is expired", email);
            throw new LoginException(ErrorMessages.EXPIRED_OTP);
        }

        if (!otp.equals(otpVerificationToken.getOtp())) {
            log.debug("User provided invalid OTP for {}", email);
            throw new LoginException(ErrorMessages.INVALID_OTP);
        }

        // prevent multiple login with same otp
        repository.deleteByEmail(email);
        log.info("Deleted used OTP for {}", email);
        log.info("Verified OTP for {}", email);
        return true;
    }
}
