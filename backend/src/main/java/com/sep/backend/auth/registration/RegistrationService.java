package com.sep.backend.auth.registration;

import com.sep.backend.ErrorMessages;
import com.sep.backend.account.AccountService;
import com.sep.backend.account.ProfilePictureStorageService;
import com.sep.backend.auth.JwtUtil;
import com.sep.backend.auth.email.EmailService;
import com.sep.backend.auth.email.EmailVerificationService;
import com.sep.backend.auth.login.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RegistrationService {
    private final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final ProfilePictureStorageService profilePictureStorageService;

    public RegistrationService(AccountService accountService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmailService emailService, EmailVerificationService emailVerificationService, ProfilePictureStorageService profilePictureStorageService) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.profilePictureStorageService = profilePictureStorageService;
    }

    /**
     * Starts the registration process by saving user information and sending verification email.
     *
     * @param data The user information.
     * @param file The optional profile picture.
     * @return
     */
    public String register(@Valid RegistrationDTO data, MultipartFile file) {
        String email = data.getEmail().toLowerCase();
        String username = data.getUsername().toLowerCase();

        // check if email already registered
        if (accountService.existsEmail(email)) {
            throw new RegistrationException(ErrorMessages.ALREADY_EXISTS_EMAIL);
        }
        // check if username already registered
        if (accountService.existsUsername(data.getUsername())) {
            throw new RegistrationException(ErrorMessages.ALREADY_EXISTS_USERNAME);
        }
        // check if the username contains @
        if (username.contains("@")) {
            throw new RegistrationException(ErrorMessages.INVALID_USERNAME_CHARACTER_AT);
        }

        // save profile picture if exists
        String profilePictureUrl = file != null ? profilePictureStorageService.save(file, username) : null;

        // hash password before saving
        data.setPassword(passwordEncoder.encode(data.getPassword()));

        accountService.createAccount(data, profilePictureUrl);

        sendVerificationEmail(email);

        return "Verification link sent.";
    }


    /**
     * Verifies the user account with the token from the mail.
     *
     * @param token The verification token.
     * @return The auth response with the access token
     */
    public AuthResponse verifyEmail(@Valid String token, HttpServletResponse res) {
        log.debug("VERIFY EMAIL: Verifying email with token {}", token);
        String email = emailVerificationService.verify(token);
        log.info("VERIFY EMAIL: Verified email {}", email);

        log.debug("VERIFY EMAIL: Getting role for {}", email);
        String role = accountService.getRoleByEmail(email);
        log.info("VERIFY EMAIL: Got role for {}", email);

        log.debug("VERIFY EMAIL: Generating refresh token for {}", email);
        String refreshToken = jwtUtil.generateRefreshToken(email, role);
        setRefreshTokenCookie(refreshToken, res);
        log.info("VERIFY EMAIL: Generated refresh token for {}", email);

        return generateAuthResponse(email, role);
    }


    /**
     * Resends a new verification link to the email.
     *
     * @param email The email.
     * @return
     */
    public String resendVerificationEmail(@Valid @Email String email) {
        log.debug("RESEND VERIFICATION EMAIL: Resending verification link for {}", email);
        sendVerificationEmail(email);
        log.info("RESEND VERIFICATION EMAIL: Resent verification link for {}", email);
        return "Verification link resent.";
    }

    /**
     * Sends a new verification link to the email.
     *
     * @param email The email.
     */
    private void sendVerificationEmail(String email) {
        var emailVerificationTokenEntity = emailVerificationService.save(email);
        String verificationToken = emailVerificationTokenEntity.getToken();
        String link = "http://localhost/verify/email?token=" + verificationToken;
        emailService.sendVerificationLink(email, link);
    }

    /**
     * Adds a "refreshToken" to the response.
     *
     * @param refreshToken The refresh token.
     * @param res          The response.
     */
    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse res) {
        log.debug("Creating new refresh token cookie");
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        // not suitable locally
        // cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        int maxAge = 7 * 24 * 60 * 60; // 7 Days
        cookie.setMaxAge(maxAge);
        log.debug("Created new refresh token cookie");

        log.debug("Adding new cookie to response");
        res.addCookie(cookie);
        log.debug("Added new cookie to response");
    }

    /**
     * Generates a new auth response for an email
     *
     * @param email The email
     * @return The auth response with access token.
     */
    private AuthResponse generateAuthResponse(String email, String role) {
        String accessToken = jwtUtil.generateAccessToken(email, role);
        return new AuthResponse(accessToken);
    }
}
