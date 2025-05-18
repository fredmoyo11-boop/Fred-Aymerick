package com.sep.backend.auth;

import com.sep.backend.ErrorMessages;
import com.sep.backend.account.AccountService;
import com.sep.backend.auth.login.LoginException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtFilter.class);


    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    public JwtFilter(JwtUtil jwtUtil, AccountService accountService) {
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String BEARER = "Bearer ";
        // skip auth on missing/incorrect auth header
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            log.debug("Authorization header missing or invalid format: \"{}\" \n Skipping JwtFilter.", authHeader);
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(BEARER.length());

        log.debug("Extracting email from JWT: {}", token);
        final String email;
        try {
            email = jwtUtil.extractEmail(token);
        } catch (ExpiredJwtException e) {
            log.debug("Provided JWT is invalid: {} ({})", e.getMessage(), token);
            throw new LoginException(ErrorMessages.INVALID_ACCESS_TOKEN);
        }
        log.debug("Extracted email from JWT: {}", email);


        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final String role = accountService.getRoleByEmail(email);

            if (role == null) {
                throw new RuntimeException("Account doesn't have a role. We made a huge mistake!");
            }

            log.debug("Validating token for email: {} ({})", email, token);
            if (jwtUtil.validateToken(token)) {
                var authority = new SimpleGrantedAuthority("ROLE_" + role);

                var authToken = new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // skip filter on routes where permitall
        if (path.startsWith("/api/auth/") || path.startsWith("/v3/api-docs") || path.startsWith("/uploads/profile/picture")) {
            log.debug("Skipping JwtFilter for permitAll path: {}", path);
            return true;
        }
        return false;
    }
}
