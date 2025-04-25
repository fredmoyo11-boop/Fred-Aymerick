package com.sep.backend.auth.login;

import com.sep.backend.ErrorMessages;
import com.sep.backend.auth.JwtUtil;
import com.sep.backend.auth.email.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LoginService {
    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    public LoginService(JwtUtil jwtUtil, OtpService otpService, EmailService emailService, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Starts the login process, by authenticating user and sending OTP.
     *
     * @param loginRequest The login request containing email and password.
     * @return
     */
    public String login(@Valid LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
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

        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);

        return "OTP sent.";
    }


    /**
     * Verifies an OTP of a user.
     *
     * @param otpRequest The OTP request with email and OTP.
     * @param response   The response to add the "refreshToken" cookie to.
     * @return The auth response with access and refresh token.
     */
    public AuthResponse verifyOtp(@Valid OtpRequest otpRequest, HttpServletResponse response) {
        String email = otpRequest.getEmail();
        String otp = otpRequest.getOtp();
        if (!otpService.validateOtp(email, otp)) {
            log.debug("{} provided invalid otp {}", email, otp);
            throw new LoginException(ErrorMessages.INVALID_OTP);
        }

        String refreshToken = jwtUtil.generateRefreshToken(email);
        setRefreshTokenCookie(refreshToken, response);

        return generateAuthResponse(email);
    }

    /**
     * Refreshes a user access token with a refresh token.
     *
     * @param req The request containing the "refreshToken" cookie
     * @return The auth response with the refreshed access token.
     */
    public AuthResponse refresh(HttpServletRequest req) {
        String refreshToken = extractRefreshToken(req);

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            log.debug("Invalid refresh token {}", refreshToken);
            throw new LoginException(ErrorMessages.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.extractEmail(refreshToken);
        log.info("Refreshed access token for {}", email);
        return generateAuthResponse(email);
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
     * Extracts the value of the "refreshToken" cookie from the request.
     *
     * @param request The request.
     * @return The value of the "refreshToken" cookie if presents, else null.
     */
    private String extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getCookies()).stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates a new auth response for an email
     *
     * @param email The email
     * @return The auth response with access token.
     */
    private AuthResponse generateAuthResponse(String email) {
        String accessToken = jwtUtil.generateAccessToken(email);
        return new AuthResponse(accessToken);
    }
}
