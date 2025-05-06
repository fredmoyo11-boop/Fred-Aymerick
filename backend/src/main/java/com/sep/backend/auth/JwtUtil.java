package com.sep.backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for JSON Web Tokens (RFC 7519)
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 min
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    /**
     * Returns the signing key for JWTs.
     *
     * @return The signing key.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generates an access JWT for the given email.
     *
     * @param email The email.
     * @return The access JWT.
     */
    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * Generates a refresh JWT for the given email.
     *
     * @param email The email.
     * @return The refresh JWT.
     */
    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * Generates a JWT for the given email with the specified expiration time.
     *
     * @param email      The email.
     * @param expiration The expiration time (in epoch millis)
     * @return The generated JWT.
     */
    private String generateToken(String email, long expiration) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts a claim from a token with the given claims resolver.
     *
     * @param token          The token.
     * @param claimsResolver The claims resolver.
     * @param <T>            The class of the claim to be extracted.
     * @return The extracted claim.
     * @throws ExpiredJwtException
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);

    }

    /**
     * Validates a JWT.
     *
     * @param token The JWT.
     * @return Whether the JWT is valid or not.
     */
    public boolean validateToken(String token) {
        return !isExpired(token);
    }

    /**
     * Returns whether the JWT is expired or not.
     *
     * @param token The JWT.
     * @return Whether the JWT is expired or not.
     */
    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Extracts an email from the token.
     *
     * @param token The token.
     * @return The extracted email.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

}
