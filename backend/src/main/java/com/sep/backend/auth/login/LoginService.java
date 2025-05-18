package com.sep.backend.auth.login;

import com.sep.backend.ErrorMessages;
import com.sep.backend.account.AccountService;
import com.sep.backend.auth.JwtUtil;
import com.sep.backend.auth.email.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Service
public class LoginService {
    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth/refresh";

    public LoginService(JwtUtil jwtUtil, OtpService otpService, EmailService emailService, AuthenticationManager authenticationManager, AccountService accountService) {
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
    }

    /**
     * Returns an optional containing email belonging to the provided unique identifier.
     *
     * @param uniqueIdentifier The unique identifier.
     * @return The optional containing the email. Might be empty if id was unknown email.
     */
    private Optional<String> getEmailByUniqueIdentifier(String uniqueIdentifier) {
        //if unique identifier contains @ it must be an email, else it is treated as a username
        if (uniqueIdentifier.contains("@")) {
            return Optional.of(uniqueIdentifier);
        } else {
            return accountService.findEmailByUsername(uniqueIdentifier);
        }
    }

    /**
     * Starts the login process by authenticating the user and sending OTP.
     *
     * @param loginRequest The login request containing email and password.
     * @return
     */
    public String login(@Valid LoginRequest loginRequest) {
        String uniqueIdentifier = loginRequest.getUniqueIdentifier();

        String email = getEmailByUniqueIdentifier(uniqueIdentifier)
                // if optional is empty, it was an invalid username, therefore, must be invalid credentials
                .orElseThrow(() -> new LoginException(ErrorMessages.INVALID_CREDENTIALS));
        String password = loginRequest.getPassword();

        log.debug("Authenticating {}", email);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new LoginException(ErrorMessages.INVALID_CREDENTIALS, e);
        }
        log.info("Authenticated {}", email);

        if (!accountService.isVerified(email)) {
            throw new LoginException(ErrorMessages.PENDING_VERIFICATION);
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);

        return "OTP sent.";
    }

    /**
     * Logs out the current user by clearing the refresh token cookie.
     *
     * @param res The response
     */
    public void logout(HttpServletResponse res) {
        log.debug("LOGOUT: Logging out current user");
        clearRefreshTokenCookie(res);
        log.debug("LOGOUT: Logged out current user");
    }


    /**
     * Verifies an OTP of a user.
     *
     * @param otpRequest The OTP request with email and OTP.
     * @param response   The response to add the "refreshToken" cookie to.
     * @return The auth response with access and refresh token.
     */
    public AuthResponse verifyOtp(@Valid OtpRequest otpRequest, HttpServletResponse response) {
        String uniqueIdentifier = otpRequest.getUniqueIdentifier();

        String email = getEmailByUniqueIdentifier(uniqueIdentifier)
                // if optional is empty, it was an invalid username, therefore, must be invalid credentials (should not be reached at any time)
                .orElseThrow(() -> new LoginException(ErrorMessages.INVALID_CREDENTIALS));
        String otp = otpRequest.getOtp();

        if (!otpService.validateOtp(email, otp)) {
            log.debug("{} provided invalid otp {}", email, otp);
            throw new LoginException(ErrorMessages.INVALID_OTP);
        }

        String role = accountService.getRoleByEmail(email);

        String refreshToken = jwtUtil.generateRefreshToken(email, role);
        setRefreshTokenCookie(refreshToken, response);

        return generateAuthResponse(email, role);
    }

    /**
     * Refreshes a user access token with a refresh token.
     *
     * @param req The request containing the "refreshToken" cookie
     * @return The auth response with the refreshed access token.
     */
    public AuthResponse refresh(HttpServletRequest req) {
        String refreshToken = extractRefreshTokenCookie(req);

        if (refreshToken == null || refreshToken.isEmpty() || !jwtUtil.validateToken(refreshToken)) {
            log.debug("Invalid refresh token {}", refreshToken);
            throw new LoginException(ErrorMessages.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.extractEmail(refreshToken);
        log.info("Refreshed access token for {}", email);
        String role = accountService.getRoleByEmail(email);
        return generateAuthResponse(email, role);
    }

    /**
     * Adds a "refreshToken" to the response.
     *
     * @param refreshToken The refresh token.
     * @param res          The response.
     */
    private void setRefreshTokenCookie(String refreshToken, HttpServletResponse res) {
        log.debug("Creating new refresh token cookie");
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        // not suitable locally
        // cookie.setSecure(true);
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        int maxAge = 7 * 24 * 60 * 60; // 7 Days
        cookie.setMaxAge(maxAge);
        log.debug("Created new refresh token cookie");

        log.debug("Adding new cookie to response");
        res.addCookie(cookie);
        log.debug("Added new cookie to response");
    }

    /**
     * Clears the refresh token cookie.
     *
     * @param res The response.
     */
    private void clearRefreshTokenCookie(HttpServletResponse res) {
        log.debug("Clearing refresh token cookie");
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setPath(REFRESH_TOKEN_COOKIE_PATH);
        cookie.setMaxAge(0);
        res.addCookie(cookie);
        log.debug("Cleared refresh token cookie");
    }

    /**
     * Extracts the value of the "refreshToken" cookie from the request.
     *
     * @param req The request.
     * @return The value of the "refreshToken" cookie if presents, else null.
     */
    private String extractRefreshTokenCookie(HttpServletRequest req) {
        // ofNullable because getCookies returns null if no cookies were set
        return Optional.ofNullable(req.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates a new auth response for an email
     *
     * @param email The email
     * @return The auth response with the access token.
     */
    private AuthResponse generateAuthResponse(String email, String role) {
        String accessToken = jwtUtil.generateAccessToken(email, role);
        return new AuthResponse(accessToken);
    }
}
