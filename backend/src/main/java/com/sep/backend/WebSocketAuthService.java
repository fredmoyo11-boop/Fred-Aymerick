package com.sep.backend;

import com.sep.backend.account.AccountService;
import com.sep.backend.auth.JwtUtil;
import com.sep.backend.auth.login.LoginException;
import com.sep.backend.entity.TripOfferEntity;
import com.sep.backend.trip.offer.TripOfferRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Slf4j
@Service
public class WebSocketAuthService {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    private static final String NOTIFICATION_TOPIC_PREFIX = "/topic/notification/";
    private static final String SIMULATION_TOPIC_PREFIX = "/topic/simulation/";

    private final AccountService accountService;
    private final TripOfferRepository tripOfferRepository;
    private final JwtUtil jwtUtil;

    public WebSocketAuthService(AccountService accountService, TripOfferRepository tripOfferRepository, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.tripOfferRepository = tripOfferRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Tries to authorize the current user for the web socket connection.
     *
     * @param accessor The stomp header accessor.
     * @throws LoginException           If provided access token is invalid.
     * @throws IllegalArgumentException If Authorization header is missing.
     * @throws IllegalArgumentException If user has an invalid role or token role does not match server role.
     */
    public void authorizeUser(StompHeaderAccessor accessor) throws LoginException, IllegalArgumentException {
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

        log.info("Successfully authenticated user {}.", email);
    }


    /**
     * Subscribes a user to a destination.
     *
     * @param accessor The stomp header accessor.
     * @throws IllegalArgumentException If the user is not authenticated.
     * @throws IllegalArgumentException If the destination is null.
     * @throws IllegalArgumentException If the user is not allowed to subscribe requested end point.
     */
    public void subscribeUser(StompHeaderAccessor accessor) throws IllegalArgumentException {
        log.debug("Intercepting SUBSCRIBE message.");

        final var auth = (Authentication) accessor.getUser();
        if (auth == null || !auth.isAuthenticated()) {
            log.debug("User is not authenticated. Cannot subscribe to destination.");
            throw new IllegalArgumentException("User is not authenticated. Cannot subscribe to destination.");
        }

        final String destination = accessor.getDestination();
        if (destination == null) {
            throw new IllegalArgumentException("Destination is null. Cannot subscribe to destination.");
        }

        validateSubscription(destination, auth);
    }

    /**
     * Validates if the current user is allowed to subscribe to the specified destination.
     *
     * @param destination The subscription destination
     * @param principal   The principal of the current user.
     * @throws IllegalArgumentException If the user is not allowed to subscribe to the specified destination.
     */
    private void validateSubscription(String destination, Principal principal) throws IllegalArgumentException {
        log.debug("Validating subscription of {} to {}.", principal.getName(), destination);
        final var subscriptionValidator = resolveSubscriptionValidator(destination);
        // if optional is empty no validation is needed
        if (subscriptionValidator.isEmpty()) {
            log.debug("No validation needed for subscription to {}.", destination);
            return;
        }

        if (!subscriptionValidator.get().apply(destination, principal)) {
            log.error("Invalid subscription for user {} to destination: {}", principal.getName(), destination);
            throw new IllegalArgumentException("Invalid subscription for user to destination: " + destination);
        }

        log.info("Valid subscription for user {} to destination: {}", principal.getName(), destination);
    }

    /**
     * Resolves the subscription validator based on the destination.
     *
     * @param destination The subscription destination.
     * @return An optional containing the validator, if destination needs validation. Else an empty optional is returned.
     */
    private Optional<BiFunction<String, Principal, Boolean>> resolveSubscriptionValidator(String destination) {
        if (destination.startsWith(NOTIFICATION_TOPIC_PREFIX)) {
            return Optional.of(this::isValidNotificationSubscription);
        } else if (destination.startsWith(SIMULATION_TOPIC_PREFIX)) {
            return Optional.of(this::isValidSimulationSubscription);
        } else {
            return Optional.empty();
        }
    }


    /**
     * Returns whether the current user can subscribe to simulation or not.
     *
     * @param destination The destination of the subscription.
     * @param principal   The principal of the current user.
     * @return Whether the current user can subscribe to simulation or not.
     */
    private boolean isValidSimulationSubscription(String destination, Principal principal) {
        return extractTripOfferId(destination)
                .filter(tripOfferId -> isPartOfTrip(tripOfferId, principal))
                .isPresent();
    }

    /**
     * Extracts the trip offer id from the destination.
     *
     * @param destination The destination of the subscription.
     * @return The optional containing the id, if id was parsable, else an empty optional.
     */
    private Optional<Long> extractTripOfferId(String destination) {
        log.debug("Extracting trip offer id from {}.", destination);
        // nasty typo ~ 1 hour
        String tripOfferId = destination.substring(SIMULATION_TOPIC_PREFIX.length());
        log.debug("Extracted trip offer id {} from {}.", tripOfferId, destination);
        try {
            return Optional.of(Long.parseLong(tripOfferId));
        } catch (NumberFormatException e) {
            log.debug("Could not parse trip offer id from {}.", tripOfferId);
            return Optional.empty();
        }
    }

    /**
     * Returns whether the current user can subscribe to notification or not.
     *
     * @param destination The destination of the subscription.
     * @param principal   The principal of the current user.
     * @return Whether the current user can subscribe to notification or not.
     */
    public boolean isValidNotificationSubscription(String destination, Principal principal) {
        String expectedDestination = NOTIFICATION_TOPIC_PREFIX + principal.getName();
        return expectedDestination.equals(destination);
    }

    /**
     * Checks if the current user is part of the trip offer with the specified id.
     * Either CUSTOMER or DRIVER.
     *
     * @param tripOfferId The id of the trip.
     * @param principal   The principal of the current user.
     * @return Whether the user is part of the trip or not.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    public boolean isPartOfTrip(Long tripOfferId, Principal principal) throws NotFoundException {
        return isPartOfTrip(tripOfferId, principal.getName());
    }

    /**
     * Checks if the current user is part of the trip offer with the specified id.
     * Either CUSTOMER or DRIVER.
     *
     * @param tripOfferId The id of the trip.
     * @param email       The email of the user.
     * @return Whether the user is part of the trip or not.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    private boolean isPartOfTrip(Long tripOfferId, String email) throws NotFoundException {
        log.debug("Getting trip offer with id {}.", tripOfferId);
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        log.debug("Got trip offer with id {}.", tripOfferId);
        var driverEntity = tripOfferEntity.getDriver();
        var customerEntity = tripOfferEntity.getTripRequest().getCustomer();
        if (List.of(driverEntity.getEmail(), customerEntity.getEmail()).contains(email)) {
            log.debug("User with email {} is part of trip with customer {} and driver {}.", email, customerEntity.getEmail(), driverEntity.getEmail());
            return true;
        } else {
            log.debug("User with email {} is not part of trip with customer {} and driver {}.", email, customerEntity.getEmail(), driverEntity.getEmail());
            return false;
        }
    }

    /**
     * Returns the trip offer entity for the specified id.
     *
     * @param id The id of the trip offer.
     * @return The trip offer entity.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    public TripOfferEntity getTripOfferEntity(Long id) throws NotFoundException {
        return tripOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
    }
}
