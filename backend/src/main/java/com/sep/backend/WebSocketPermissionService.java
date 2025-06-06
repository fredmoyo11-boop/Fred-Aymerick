package com.sep.backend;

import com.sep.backend.account.AccountService;
import com.sep.backend.entity.TripOfferEntity;
import com.sep.backend.trip.offer.TripOfferRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class WebSocketPermissionService {

    private final TripOfferRepository tripOfferRepository;
    private final AccountService accountService;

    private static final String NOTIFICATION_TOPIC_PREFIX = "/topic/notification/";
    private static final String SIMULATION_TOPIC_PREFIX = "/topic/simulation/";


    public WebSocketPermissionService(TripOfferRepository tripOfferRepository, AccountService accountService) {
        this.tripOfferRepository = tripOfferRepository;
        this.accountService = accountService;
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
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        var driverEntity = tripOfferEntity.getDriver();
        var customerEntity = tripOfferEntity.getTripRequest().getCustomer();
        return List.of(driverEntity.getEmail(), customerEntity.getEmail()).contains(email);
    }

    /**
     * Returns the role the user with the specified email has for the trip with the
     * specified id. Either CUSTOMER or DRIVER.
     *
     * @param tripOfferId The id of the trip offer.
     * @param email       The email of the user.
     * @return The present role if the user is part of the trip, empty otherwise.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    public Optional<String> findRoleOfTrip(Long tripOfferId, String email) throws NotFoundException {
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        var driverEmail = tripOfferEntity.getDriver().getEmail();
        var customerEmail = tripOfferEntity.getTripRequest().getCustomer().getEmail();
        if (driverEmail.equals(email)) {
            return Optional.of(Roles.DRIVER);
        } else if (customerEmail.equals(email)) {
            return Optional.of(Roles.CUSTOMER);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Return the role the principal has for the trip with the specified id. Either
     * CUSTOMER or DRIVER.
     *
     * @param tripOfferId The id of the trip.
     * @param principal   The principal of the current user.
     * @return The present role if the user is part of the trip, empty otherwise.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    public Optional<String> findRoleOfTrip(Long tripOfferId, Principal principal) throws NotFoundException {
        return findRoleOfTrip(tripOfferId, principal.getName());
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

    /**
     * Returns whether the current user can subscribe to simulation or not.
     *
     * @param destination The destination of the subscription.
     * @param principal   The principal of the current user.
     * @return Whether the current user can subscribe to simulation or not.
     */
    public boolean isValidSimulationSubscription(String destination, Principal principal) {
        if (destination == null || !destination.startsWith(SIMULATION_TOPIC_PREFIX)) {
            return false;
        }

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
    public Optional<Long> extractTripOfferId(String destination) {
        String tripOfferId = destination.substring(NOTIFICATION_TOPIC_PREFIX.length());
        try {
            return Optional.of(Long.parseLong(tripOfferId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public boolean isValidNotificationSubscription(String destination, String email) {
        if (destination == null || !destination.startsWith(NOTIFICATION_TOPIC_PREFIX)) {
            return false;
        }
        String expectedDestination = NOTIFICATION_TOPIC_PREFIX + email;
        return expectedDestination.equals(destination);
    }
}
