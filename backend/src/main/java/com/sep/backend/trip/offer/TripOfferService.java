package com.sep.backend.trip.offer;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.DriverStatistics;
import com.sep.backend.entity.*;
import com.sep.backend.notification.NotificationService;
import com.sep.backend.trip.offer.status.*;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.request.TripRequestRepository;
import com.sep.backend.trip.request.TripRequestStatus;
import com.sep.backend.notification.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class TripOfferService {
    private final DriverRepository driverRepository;
    private final TripRequestRepository tripRequestRepository;
    private final TripOfferRepository tripOfferRepository;
    private final NotificationService notificationService;

    public TripOfferService(TripOfferRepository tripOfferRepository, DriverRepository driverRepository, TripRequestRepository tripRequestRepository, NotificationService notificationService) {
        this.tripOfferRepository = tripOfferRepository;
        this.driverRepository = driverRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.notificationService = notificationService;
    }

    public TripOffer getAcceptedTripOffer(Long tripRequestId) throws NotFoundException {
        var tripOffers = tripOfferRepository.findByTripRequest_IdAndStatus(tripRequestId, TripOfferStatus.ACCEPTED);
        if (tripOffers.isEmpty()) {
            throw new NotFoundException("No accepted offer found for trip request id: " + tripRequestId);
        } else if (tripOffers.size() >= 2) {
            throw new IllegalStateException("Too many accepted offer found for trip request id: " + tripRequestId);
        } else {
            return TripOffer.from(tripOffers.getFirst());
        }
    }

    public TripOffer getCurrentActiveTripOffer(Principal principal) throws NotFoundException {
        var tripOffers = Stream.of(getCurrentPendingTripOffer(principal), getCurrentAcceptedTripOffer(principal))
                .flatMap(Optional::stream)
                .map(TripOffer::from)
                .toList();

        if (tripOffers.isEmpty()) {
            throw new NotFoundException("Current driver does not have a pending trip offer.");
        } else if (tripOffers.size() >= 2) {
            throw new RuntimeException("Too many active trip offers. We messed up the integrity.");
        } else {
            return tripOffers.getFirst();
        }
    }

    public Optional<TripOfferEntity> getCurrentPendingTripOffer(Principal principal) {
        String email = principal.getName();
        var tripOffers = tripOfferRepository.findByDriver_EmailAndStatus(email, TripOfferStatus.PENDING);
        if (tripOffers.size() >= 2) {
            throw new IllegalStateException("Too many pending trip offers.");
        } else if (tripOffers.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(tripOffers.getFirst());
        }
    }

    public Optional<TripOfferEntity> getCurrentAcceptedTripOffer(Principal principal) {
        String email = principal.getName();
        var tripOffers = tripOfferRepository.findByDriver_EmailAndStatus(email, TripOfferStatus.ACCEPTED);
        if (tripOffers.size() >= 2) {
            throw new IllegalStateException("Too many active trip offers.");
        } else if (tripOffers.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(tripOffers.getFirst());
        }
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

    /**
     * Returns the trip offer entity for the specified id.
     *
     * @param id The id of the trip offer.
     * @return The trip offer entity.
     * @throws NotFoundException If a trip offer with specified id does not exist.
     */
    public TripOfferEntity getTripOffer(Long id) throws NotFoundException {
        return tripOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_TRIP_OFFER));
    }

    public String createNewTripOffer(Long tripRequestId, Principal principal) throws ForbiddenException, NotFoundException {
        if (checkIfActiveTripOfferExists(principal.getName())) {
            throw new ForbiddenException("Driver has an active trip offer.");
        }
        TripRequestEntity tripRequestEntity = tripRequestRepository.findById(tripRequestId)
                .orElseThrow(() -> new NotFoundException("Trip request not found."));
        DriverEntity driverEntity = driverRepository.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new NotFoundException("Driver not found."));
        TripOfferEntity tripOfferEntity = new TripOfferEntity(tripRequestEntity, driverEntity, TripOfferStatus.PENDING);
        tripOfferRepository.save(tripOfferEntity);

        var customerEntity = tripRequestEntity.getCustomer();

        var customerNotification = new Notification();
        customerNotification.setNotificationType(NotificationTypes.TRIP_OFFER_NEW);
        String customerMessage = String.format("Du hast ein neues Fahrtangebot von %s %s (%s)!", driverEntity.getFirstName(), driverEntity.getLastName(), driverEntity.getUsername());
        customerNotification.setMessage(customerMessage);
        notificationService.sendNotification(customerNotification, customerEntity.getEmail());

        var driverNotification = new Notification();
        driverNotification.setNotificationType(NotificationTypes.TRIP_OFFER_NEW);
        String driverMessage = String.format("Das Fahrtangebot für %s %s (%s) wurde erstellt!", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getUsername());
        driverNotification.setMessage(driverMessage);
        notificationService.sendNotification(driverNotification, driverEntity.getEmail());
        return "Successfully created new trip offer.";
    }

    @Transactional
    public void acceptTripOffer(Long tripOfferId, Principal principal) throws NotFoundException {
        if (!isPendingTripOffer(tripOfferId)) {
            throw new RuntimeException("Trip offer is not pending.");
        }

        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        if (!tripOfferEntity.getTripRequest().getCustomer().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Trip offer does not belong to trip request of the current customer.");
        }

        tripOfferEntity.setStatus(TripOfferStatus.ACCEPTED);
        final var updatedTripOfferEntity = tripOfferRepository.saveAndFlush(tripOfferEntity);

        Long tripRequestId = updatedTripOfferEntity.getTripRequest().getId();
        var tripOffers = tripOfferRepository.findByTripRequest_IdAndStatus(tripRequestId, TripOfferStatus.PENDING)
                .stream().peek(tripOffer -> {
                    tripOffer.setStatus(TripOfferStatus.REJECTED);

                    var notification = new Notification();
                    notification.setNotificationType(NotificationTypes.TRIP_OFFER_REJECTED);
                    String message = "Dein Fahrangebot wurde abgelehnt!";
                    notification.setMessage(message);
                    notificationService.sendNotification(notification, updatedTripOfferEntity.getDriver().getEmail());
                }).toList();
        tripOfferRepository.saveAll(tripOffers);

        var driverEntity = updatedTripOfferEntity.getDriver();
        var customerEntity = updatedTripOfferEntity.getTripRequest().getCustomer();

        var driverNotification = new Notification();
        driverNotification.setNotificationType(NotificationTypes.TRIP_OFFER_ACCEPTED);
        String acceptedNotificationDriverMessage = String.format("Dein Fahrtangebot an %s %s (%s) wurde akzeptiert! Du kannst die Simulation gleich starten!", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getUsername());
        driverNotification.setMessage(acceptedNotificationDriverMessage);
        notificationService.sendNotification(driverNotification, driverEntity.getEmail());

        var customerNotification = new Notification();
        customerNotification.setNotificationType(NotificationTypes.TRIP_OFFER_ACCEPTED);
        String acceptedNotificationCustomerMessage = String.format("Du hast das Fahrtangebot von %s %s (%s) akzeptiert! Du kannst die Simulation gleich starten!", driverEntity.getFirstName(), driverEntity.getLastName(), driverEntity.getUsername());
        customerNotification.setMessage(acceptedNotificationCustomerMessage);
        notificationService.sendNotification(customerNotification, customerEntity.getEmail());
    }

    public void rejectTripOffer(Long tripOfferId, Principal principal) throws NotFoundException {
        if (!isPendingTripOffer(tripOfferId)) {
            throw new RuntimeException("Trip offer is not pending.");
        }

        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        if (!tripOfferEntity.getTripRequest().getCustomer().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Trip offer does not belong to trip request of the current customer.");
        }

        tripOfferEntity.setStatus(TripOfferStatus.REJECTED);
        tripOfferRepository.save(tripOfferEntity);

        var driverEntity = tripOfferEntity.getDriver();
        var customerEntity = tripOfferEntity.getTripRequest().getCustomer();

        var driverNotification = new Notification();
        driverNotification.setNotificationType(NotificationTypes.TRIP_OFFER_REJECTED);
        String driverMessage = String.format("Dein Fahrtangebot an %s %s (%s) wurde abgelehnt!", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getUsername());
        driverNotification.setMessage(driverMessage);
        notificationService.sendNotification(driverNotification, driverEntity.getEmail());

        var customerNotification = new Notification();
        customerNotification.setNotificationType(NotificationTypes.TRIP_OFFER_REJECTED);
        String customerMessage = String.format("Du hast das Fahrtangebot von %s %s (%s) abgelehnt!", driverEntity.getFirstName(), driverEntity.getLastName(), driverEntity.getUsername());
        customerNotification.setMessage(customerMessage);
        notificationService.sendNotification(customerNotification, customerEntity.getEmail());
    }

    public void revokeTripOffer(Long tripOfferId, Principal principal) throws NotFoundException {
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        if (!tripOfferEntity.getDriver().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Trip offer does not belong to the current driver.");
        }

        if (!TripOfferStatus.PENDING.equals(tripOfferEntity.getStatus())) {
            throw new RuntimeException("Trip offer is not pending.");
        }

        tripOfferEntity.setStatus(TripOfferStatus.REVOKED);
        tripOfferRepository.save(tripOfferEntity);

        var customerNotification = new Notification();
        customerNotification.setNotificationType(NotificationTypes.TRIP_OFFER_REVOKED);
        var customerEntity = tripOfferEntity.getTripRequest().getCustomer();
        String customerMessage = String.format("%s %s (%s) hat sein Fahrtangebot zurückgezogen!", customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getUsername());
        customerNotification.setMessage(customerMessage);
        notificationService.sendNotification(customerNotification, customerEntity.getEmail());

        var driverNotification = new Notification();
        driverNotification.setNotificationType(NotificationTypes.TRIP_OFFER_REVOKED);
        var driverEntity = tripOfferEntity.getDriver();
        String driverMessage = String.format("Du hast dein Fahrtangebot für %s %s (%s) zurückgezogen!", driverEntity.getFirstName(), driverEntity.getLastName(), driverEntity.getUsername());
        driverNotification.setMessage(driverMessage);
        notificationService.sendNotification(driverNotification, driverEntity.getEmail());
    }

    public boolean isPendingTripOffer(Long tripOfferId) throws NotFoundException {
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        return TripOfferStatus.PENDING.equals(tripOfferEntity.getStatus());
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
        setStatus(tripOfferEntity, TripOfferStatus.REVOKED);
        return "Successfully withdrawn trip offer";
    }

    /**
     * Sets offer status from given offer and status String
     *
     * @param tripOfferEntity trip offer to set the status
     * @param status          status to set
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
    public List<TripOffer> getTripOfferList(Principal principal) {
        return tripOfferRepository.findAllByTripRequest_Customer_EmailAndStatus(principal.getName(), TripOfferStatus.PENDING).stream()
                .map(entity -> {
                    var tripOffer = TripOffer.from(entity);
                    var driverEntity = entity.getDriver();
                    var driverStatistics = getDriverStatistics(driverEntity);
                    tripOffer.setDriverStatistics(driverStatistics);
                    return tripOffer;
                }).toList();
    }

    private DriverStatistics getDriverStatistics(DriverEntity driverEntity) {
        var driverStatistics = new DriverStatistics();
        driverStatistics.setDriverUsername(driverEntity.getUsername());
        driverStatistics.setDriverFirstName(driverEntity.getFirstName());
        driverStatistics.setDriverLastName(driverEntity.getLastName());
        driverStatistics.setAverageRating(tripOfferRepository.getAvgRatingByDriver_TripHistory(driverEntity.getId()));
        driverStatistics.setTotalTrips(tripOfferRepository.getTotalDriveCountByDriver_TripHistory(driverEntity.getId()));
        driverStatistics.setTotalDistance(tripOfferRepository.getTotalDriveDistanceByDriver_TripHistory(driverEntity.getId()));
        return driverStatistics;
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
        var tripOfferEntity = getTripOfferEntity(tripOfferId);
        setStatus(tripOfferEntity, TripOfferStatus.COMPLETED);

        TripRequestEntity tripRequestEntity = tripOfferEntity.getTripRequest();
        tripRequestEntity.setStatus(TripRequestStatus.COMPLETED);
        tripRequestRepository.save(tripRequestEntity);
    }

}
