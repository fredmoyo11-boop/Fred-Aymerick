package com.sep.backend.trip.simulation;

import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.balance.BalanceService;
import com.sep.backend.trip.history.TripHistoryService;
import com.sep.backend.trip.offer.TripOfferService;
import com.sep.backend.trip.simulation.data.SimulationAction;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TripSimulationService {

    private final ActionStore actionStore;
    private final BalanceService balanceService;
    private final TripHistoryService tripHistoryService;
    private final TripOfferService tripOfferService;


    public TripSimulationService(ActionStore actionStore, BalanceService balanceService, TripHistoryService tripHistoryService, TripOfferService tripOfferService) {
        this.actionStore = actionStore;
        this.balanceService = balanceService;
        this.tripHistoryService = tripHistoryService;
        this.tripOfferService = tripOfferService;
    }

    /**
     * Completes the trip for the current user by creating a trip history entity and transferring the price to the driver.
     *
     * @param tripOfferId The id of the trip offer.
     */
    @Transactional
    public void completeTrip(Long tripOfferId) {
        // User might abort before actually rating leading to a default of 5 stars, if user rates method below is used to set actual rating
        log.debug("COMPLETE: Completing trip for trip offer with id {}.", tripOfferId);
        var tripHistoryEntity = tripHistoryService.createTripHistory(tripOfferId, LocalDateTime.now(), 5, 5);
        log.debug("COMPLETE: Created trip history entity for trip offer with id {}.", tripOfferId);

        // marks both request and offer as completed
        log.debug("COMPLETE: Marking trip request and offer as completed for trip offer with id {}.", tripOfferId);
        tripOfferService.completeTripOffer(tripOfferId);
        log.debug("COMPLETE: Marked trip request and offer as completed for trip offer with id {}.", tripOfferId);


        var customerEmail = tripHistoryEntity.getCustomer().getEmail();
        var driverUsername = tripHistoryEntity.getDriver().getUsername();
        log.debug("COMPLETE: Transferring price from customer {} to driver {} for trip offer with id {}.", customerEmail, driverUsername, tripOfferId);
        balanceService.transfer(tripHistoryEntity.getPrice(), customerEmail, driverUsername);
        log.info("COMPLETE: Transferred price from customer {} to driver {} for trip offer with id {}.", customerEmail, driverUsername, tripOfferId);
    }


    /**
     * Sets the rating for the current user for the trip history belonging to trip offer with the specified id.
     *
     * @param tripOfferId The id of the trip offer.
     * @param rating      The rating to set. Must be between 1 and 5.
     * @param email       The email of the current user.
     * @throws NotFoundException If a trip history belonging to trip offer with the specified id does not exist.
     */
    public void rateTrip(Long tripOfferId, Integer rating, String email) throws NotFoundException {
        log.debug("RATE: Checking if rating is between 1 and 5. Rating: {}.", rating);
        if (!Range.of(1, 5).contains(rating)) {
            log.error("RATE: Rating must be between 1 and 5. Rating: {}.", rating);
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        log.debug("RATE: Rating is between 1 and 5. Rating: {}.", rating);

        log.debug("RATE: Checking if user is part of trip offer {}. Email: {}.", tripOfferId, email);
        var tripHistoryEntity = tripHistoryService.findByTripOfferId(tripOfferId)
                .orElseThrow(() -> {
                    log.error("RATE: Trip history not found for trip offer with id: {}.", tripOfferId);
                    return new NotFoundException("Trip history not found for trip offer with id: " + tripOfferId);
                });
        log.debug("RATE: User is part of trip offer {}. Email: {}", tripOfferId, email);

        log.debug("RATE: Searching role for user {} in trip offer {}.", email, tripOfferId);
        String role = tripOfferService.findRoleOfTrip(tripOfferId, email)
                .orElseThrow(() -> {
                    log.error("RATE: User is not part of trip offer with id: {}.", tripOfferId);
                    return new RuntimeException("User is not part of trip offer with id: " + tripOfferId + ".");
                });
        log.debug("RATE: Found role {} for user {} in trip offer {}.", role, email, tripOfferId);

        log.debug("RATE: Setting rating for user {} in trip offer {} to {}.", email, tripOfferId, rating);
        switch (role) {
            case Roles.CUSTOMER -> tripHistoryEntity.setCustomerRating(rating);
            case Roles.DRIVER -> tripHistoryEntity.setDriverRating(rating);
        }
        log.debug("RATE: Rating for user {} in trip offer {} set to {}.", email, tripOfferId, rating);

        log.debug("RATE: Updating trip history entity for trip offer {}.", tripOfferId);
        tripHistoryService.save(tripHistoryEntity);
        log.debug("RATE: Updated trip history entity for trip offer {}.", tripOfferId);
    }


    public SimulationAction sendSimulationAction(Long tripOfferId, SimulationAction action, Principal principal) {
        log.debug("Received simulation action {} for trip offer {} from {}.", action.getActionType(), tripOfferId, principal.getName());
        log.debug("Checking if user is part of trip offer {}. Principal: {}.", tripOfferId, principal.getName());
        log.debug("User is part of trip offer? {}.", tripOfferService.isPartOfTrip(tripOfferId, principal));
        actionStore.addAction(tripOfferId, action);
        return action;
    }

    public List<SimulationAction> getSimulationActions(Long tripOfferId) {
        return actionStore.getActionsByTrip(tripOfferId);
    }
}
