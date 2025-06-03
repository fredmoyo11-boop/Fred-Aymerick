package com.sep.backend;

import com.sep.backend.account.AccountService;
import com.sep.backend.auth.JwtUtil;
import com.sep.backend.auth.login.LoginException;
import com.sep.backend.trip.offer.TripOfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtUtil jwtUtil;
    private final AccountService accountService;
    private final TripOfferService tripOfferService;

    public AuthChannelInterceptor(JwtUtil jwtUtil, AccountService accountService, TripOfferService tripOfferService) {
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
        this.tripOfferService = tripOfferService;
    }

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String NOTIFICATION_TOPIC_PREFIX = "/topic/notification/";
    private static final String SIMULATION_TOPIC_PREFIX = "/topic/simulation/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();

        log.debug("Intercepting STOMP message.");

        accessor.toNativeHeaderMap().forEach((key, valueList) -> log.debug("Native Header: {} = {}", key, valueList));

        accessor.getMessageHeaders().forEach((key, value) -> log.debug("General Header: {} = {}", key, value));

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("Intercepting CONNECT message.");
            final String authHeader = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER_NAME);

            log.debug("Checking if auth header is present and starts with Bearer");
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                log.debug("Missing or invalid Authorization header on connect. Auth header: {}", authHeader);
                throw new IllegalArgumentException("Missing or invalid Authorization header on connect.");
            }
            log.debug("Auth header present and starts with Bearer. Extracting email from JWT.");

            final String token = authHeader.substring(BEARER_PREFIX.length());

            final String email = jwtUtil.safeExtractEmail(token)
                    .orElseThrow(() -> new LoginException(ErrorMessages.INVALID_ACCESS_TOKEN));
            // when email was extracted from the token, it was valid
            log.debug("Email extracted from JWT: {}", email);

            final String role = jwtUtil.safeExtractRole(token)
                    .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.INVALID_ROLE));
            log.debug("Role extracted from JWT: {}", role);
            // check if the role from token matches the account role
            log.debug("Checking if role in token matches account role.");
            if (!role.equals(accountService.getRoleByEmail(email))) {
                log.debug("Role in token does not match account role. Token role: {}, account role: {}", role,
                        accountService.getRoleByEmail(email));
                throw new IllegalArgumentException("Role in token does not match account role.");
            }
            log.debug("Role in token matches account role. Setting authentication for user.");

            var authority = new SimpleGrantedAuthority("ROLE_" + role);
            var authentication = new UsernamePasswordAuthenticationToken(email, null,
                    Collections.singletonList(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);

            log.debug("Authentication set for user.");
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {
            log.debug("Intercepting SUBSCRIBE message.");

            final var auth = (Authentication) accessor.getUser();
            if (auth == null || !auth.isAuthenticated()) {
                log.debug("User is not authenticated. Cannot subscribe to destination.");
                throw new IllegalArgumentException("User is not authenticated. Cannot subscribe to destination.");
            }

            final String destination = accessor.getDestination();
            final String email = auth.getName();

            if (destination == null) {
                throw new IllegalArgumentException("Destination is null. Cannot subscribe to destination.");
            }

            if (destination.startsWith(NOTIFICATION_TOPIC_PREFIX) && !isValidNotificationSubscription(destination, email)) {
                log.debug("NOTIFICATION: Invalid subscription for user {} to destination: {}", email, destination);
                throw new IllegalArgumentException("Invalid subscription for user to destination: " + destination);
            }

            if (destination.startsWith(SIMULATION_TOPIC_PREFIX) && !isValidSimulationSubscription(destination, auth)) {
                log.debug("SIMULATION: Invalid subscription for user {} to destination: {}", email, destination);
                throw new IllegalArgumentException("Invalid subscription for user to destination: " + destination);
            }


            log.debug("Valid subscription for user {} to destination: {}", email, destination);
        }

        return message;
    }

    /**
     * Returns whether the current user can subscribe to simulation or not.
     *
     * @param destination The destination of the subscription.
     * @param principal   The principal of the current user.
     * @return Whether the current user can subscribe to simulation or not.
     */
    private boolean isValidSimulationSubscription(String destination, Principal principal) {
        if (destination == null || !destination.startsWith(SIMULATION_TOPIC_PREFIX)) {
            return false;
        }

        return extractTripOfferId(destination)
                .filter(tripOfferId -> tripOfferService.isPartOfTrip(tripOfferId, principal))
                .isPresent();
    }

    /**
     * Extracts the trip offer id from the destination.
     *
     * @param destination The destination of the subscription.
     * @return The optional containing the id, if id was parsable, else an empty optional.
     */
    private Optional<Long> extractTripOfferId(String destination) {
        String tripOfferId = destination.substring(NOTIFICATION_TOPIC_PREFIX.length());
        try {
            return Optional.of(Long.parseLong(tripOfferId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private boolean isValidNotificationSubscription(String destination, String email) {
        if (destination == null || !destination.startsWith(NOTIFICATION_TOPIC_PREFIX)) {
            return false;
        }
        String expectedDestination = NOTIFICATION_TOPIC_PREFIX + email;
        return expectedDestination.equals(destination);
    }
}
