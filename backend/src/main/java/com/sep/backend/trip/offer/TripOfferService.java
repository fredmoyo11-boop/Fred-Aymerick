package com.sep.backend.trip.offer;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.entity.*;
import com.sep.backend.notification.NotificationService;
import com.sep.backend.trip.offer.status.*;
import com.sep.backend.trip.offer.response.*;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.request.TripRequestRepository;
import com.sep.backend.trip.request.TripRequestStatus;
import com.sep.backend.entity.NotificationEntity;
import com.sep.backend.notification.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.sep.backend.account.AccountService;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class TripOfferService {
    private final DriverRepository driverRepository;
    private final TripRequestRepository tripRequestRepository;
    private final TripOfferRepository tripOfferRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public TripOfferService(TripOfferRepository tripOfferRepository, AccountService accountService, DriverRepository driverRepository, TripRequestRepository tripRequestRepository, NotificationService notificationService) {
        this.tripOfferRepository = tripOfferRepository;
        this.accountService = accountService;
        this.driverRepository = driverRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.notificationService = notificationService;
    }

    /**
     * Checks if driver has a pending offer or not.
     *
     * @param principal The principal of the current user.
     * @return If an offer exists or not.
     */
    public Boolean hasActiveTripOffer(Principal principal) {
//        if(checkIfActiveTripOfferExists(principal.getName())) {
//            return TripOfferPresenceStatus.HAS_ACTIVE_OFFER;
//        }
//        return TripOfferPresenceStatus.NO_ACTIVE_OFFER;
        return checkIfActiveTripOfferExists(principal.getName());
    }

    public String createNewTripOffer(Long tripRequestId, Principal principal) throws ForbiddenException, NotFoundException {
        if(checkIfActiveTripOfferExists(principal.getName())) {
            throw new ForbiddenException("Driver has an active trip offer.");
        }
        TripRequestEntity tripRequestEntity = tripRequestRepository.findById(tripRequestId)
                                                                   .orElseThrow(() -> new NotFoundException("Trip request not found."));
        DriverEntity driverEntity = driverRepository.findByEmailIgnoreCase(principal.getName())
                                                    .orElseThrow(() -> new NotFoundException("Driver not found."));
        TripOfferEntity tripOfferEntity = new TripOfferEntity(tripRequestEntity, driverEntity, TripOfferStatus.PENDING);
        tripOfferRepository.save(tripOfferEntity);
        notificationService.sendNotification(Notification.from(new NotificationEntity(NotificationTypes.TRIP_OFFER_NEW, "New trip offer from " + driverEntity.getFirstName() + " " + driverEntity.getLastName(), tripRequestEntity.getCustomer(), null)), tripRequestEntity.getCustomer().getEmail());
        return "Successfully created new trip offer.";
    }

    /**
     * Accepts an offer and declines all other offers for a customer.
     *
     * @param driverUsername Username of a driver
     * @param principal Identifier of a customer
     * @return StringResponse confirming success
     * @throws NotFoundException when trip offer does not exist
     */
    public String acceptOffer(String driverUsername, Principal principal) throws NotFoundException {
        TripOfferEntity tripOfferEntity = tripOfferRepository.findByDriver_UsernameAndTripRequest_Customer_Email(driverUsername, principal.getName())
                                                             .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
        setStatus(tripOfferEntity, TripOfferStatus.ACCEPTED);
        List<TripOfferEntity> otherEntities = tripOfferRepository.findAllByTripRequest_Customer_EmailAndStatus(principal.getName(), TripOfferStatus.PENDING);
        for(TripOfferEntity otherEntity : otherEntities) {
            otherEntity.setStatus(TripOfferStatus.DECLINED);
        }
        tripOfferRepository.saveAll(otherEntities);
        return "Successfully accepted trip offer";
    }

    /**
     * Declines an offer.
     *
     * @param driverUsername Username of a driver
     * @param principal Identifier of a customer
     * @return StringResponse confirming success
     * @throws NotFoundException when trip offer does not exist
     */
    public String declineOffer(String driverUsername, Principal principal) throws NotFoundException {
        TripOfferEntity tripOfferEntity = tripOfferRepository.findByDriver_UsernameAndTripRequest_Customer_Email(driverUsername, principal.getName())
                                                             .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
        setStatus(tripOfferEntity, TripOfferStatus.DECLINED);
        return "Successfully declined trip offer";
    }

    /**
     * Withdraws an offer
     *
     * @param principal Identifier of a driver
     * @return StringResponse confirming success
     * @throws NotFoundException when trip offer does not exist
     */
    public String withdrawOffer(Principal principal) throws NotFoundException {
        TripOfferEntity tripOfferEntity = tripOfferRepository.findByDriver_Email(principal.getName())
                                                             .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
        setStatus(tripOfferEntity, TripOfferStatus.WITHDRAWN);
        return "Successfully withdrawn trip offer";
    }

    /**
     * Sets offer status from given offer and status String
     *
     * @param tripOfferEntity trip offer to set the status
     * @param status status to set
     */
    private void setStatus(TripOfferEntity tripOfferEntity, String status) {
        tripOfferEntity.setStatus(status);
        tripOfferRepository.save(tripOfferEntity);
    }

    /**
     * Returns the full trip offer list for a given customer
     *
     * @param principal identifier of the customer
     * @return StringResponse confirming success
     */
    public List<TripOfferResponse> getTripOfferList(Principal principal) {
        List<TripOfferResponse> tripOffers = new ArrayList<TripOfferResponse>();
        List<TripOfferEntity> tripOfferEntities = tripOfferRepository.findAllByTripRequest_Customer_EmailAndStatus(principal.getName(), TripOfferStatus.PENDING);
        for(TripOfferEntity tripOfferEntity : tripOfferEntities) {
            DriverEntity driver = driverRepository.getById(tripOfferEntity.getDriver().getId());
            tripOffers.add(new TripOfferResponse(driver.getUsername(),
                                                 driver.getFirstName(),
                                                 driver.getLastName(),
                                                 tripOfferRepository.getAvgRatingByDriver_TripHistory(driver.getId()),
                                                 tripOfferRepository.getTotalDriveCountByDriver_TripHistory(driver.getId()),
                                                 tripOfferRepository.getTotalDriveDistanceByDriver_TripHistory(driver.getId())));
        }
        return tripOffers;
    }

    /**
     * Checks if an active trip offer exists for a given driver
     *
     * @param email email of driver
     * @return whether the driver has a trip offer or not
     */
    private boolean checkIfActiveTripOfferExists(String email) {
        return (tripOfferRepository.existsByDriver_EmailAndStatus(email, TripOfferStatus.PENDING) || tripOfferRepository.existsByDriver_EmailAndStatus(email, TripOfferStatus.ACCEPTED));
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
     * Completes a trip by changing the status of an offer and request to COMPLETED.
     *
     * @param tripOfferId The id of the trip offer.
     * @throws NotFoundException If the trip offer with the specified id does not
     *                           exist.
     */
    public void completeTripOffer(Long tripOfferId) throws NotFoundException {
        TripOfferEntity tripOfferEntity = tripOfferRepository.findById(tripOfferId).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
        setStatus(tripOfferEntity, TripOfferStatus.COMPLETED);

        TripRequestEntity tripRequestEntity = tripOfferEntity.getTripRequest();
        tripRequestEntity.setStatus(TripRequestStatus.COMPLETED);
        tripRequestRepository.save(tripRequestEntity);
    }
	
}
