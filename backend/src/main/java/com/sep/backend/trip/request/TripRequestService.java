package com.sep.backend.trip.request;

import com.sep.backend.CarTypes;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.route.RouteService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;
    private final LocationRepository locationRepository;

    private final AccountService accountService;
    private final RouteService routeService;

    public TripRequestService(TripRequestRepository tripRequestRepository, LocationRepository locationRepository, AccountService accountService, RouteService routeService) {
        this.tripRequestRepository = tripRequestRepository;
        this.locationRepository = locationRepository;
        this.accountService = accountService;
        this.routeService = routeService;
    }

    /**
     * Returns whether the user already has A active request or not.
     *
     * @param email The email address
     * @return Whether the customer has A active request or not
     */
    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_EmailAndStatus(email, TripRequestStatus.ACTIVE);
    }

    /**
     * Returns the trip request entity by email and status.
     *
     * @param email The email to find the trip request entity.
     * @param requestStatus The status of the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findTripRequestByEmailAndStatus(String email, String requestStatus) {
        return tripRequestRepository.findByCustomer_EmailAndStatus(email, requestStatus);
    }

    /**
     * Returns the trip request entity by email.
     *
     * @param email The email to find the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findActiveTripRequestByEmail(String email) {
        return findTripRequestByEmailAndStatus(email, TripRequestStatus.ACTIVE);
    }

    /**
     * Returns the trip request entity in form of a trip request DTO.
     *
     * @param principal The user.
     * @return The trip request DTO.
     * @throws NotFoundException If no active trip request entity found
     */
    public TripRequestDTO getCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        Optional<TripRequestEntity> tripRequestEntity = findActiveTripRequestByEmail(email);
        return tripRequestEntity.map(TripRequestDTO::from).orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
    }

    /**
     * Creates a trip request.
     *
     * @param tripRequestBody The main body containing trip request information.
     * @param principal The user.
     * @return The trip request entity
     */
    @Transactional
    public TripRequestEntity createCurrentTripRequest(@Valid TripRequestBody tripRequestBody, Principal principal) throws TripRequestException {
        String email = principal.getName();

        String role = accountService.getRoleByEmail(email);
        //checks if role of user is customer
        if (!Roles.CUSTOMER.equals(role)) {
            throw new TripRequestException("User must be a customer.");
        }
        //checks if car type in request is valid
        if (!CarTypes.isValidCarType(tripRequestBody.getCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }
        //only one active trip request at a time
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIP_REQUEST);
        }

        var customer = accountService.getCustomerByEmail(email);
        log.debug("Creating route for trip request {}", tripRequestBody.getLocations());
        var routeEntity = routeService.createRoute(tripRequestBody.getGeojson(), tripRequestBody.getLocations());
        log.debug("Created route for trip request. Locations: {}.", tripRequestBody.getLocations() );

        double price = (routeEntity.getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDistance() / 1000.0)
                * CarTypes.getPricePerKilometer(tripRequestBody.getCarType());

        var tripRequestEntity = new TripRequestEntity();
        tripRequestEntity.setCustomer(customer);
        tripRequestEntity.setRoute(routeEntity);
        tripRequestEntity.setRequestTime(LocalDateTime.now());
        tripRequestEntity.setCarType(tripRequestBody.getCarType());
        tripRequestEntity.setStatus(TripRequestStatus.ACTIVE);
        tripRequestEntity.setNote(tripRequestBody.getNote());
        tripRequestEntity.setPrice(price);

        return tripRequestRepository.save(tripRequestEntity);
    }

    /**
     * Deletes the current active trip request.
     *
     * @param principal The user.
     * @throws NotFoundException If no active trip request found.
     */
    public void deleteCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        TripRequestEntity tripRequestEntity = findActiveTripRequestByEmail(email)
                .orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
       tripRequestEntity.setStatus(TripRequestStatus.DELETED);

        tripRequestRepository.save(tripRequestEntity);
    }


}
