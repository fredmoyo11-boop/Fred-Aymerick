package com.sep.backend.trip.request;

import com.sep.backend.CarTypes;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.entity.LocationEntity;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;
    private final LocationRepository locationRepository;

    private final AccountService accountService;

    public TripRequestService(TripRequestRepository tripRequestRepository, LocationRepository locationRepository, AccountService accountService) {
        this.tripRequestRepository = tripRequestRepository;
        this.locationRepository = locationRepository;
        this.accountService = accountService;
    }

    /**
     * Returns whether the user already has A active request or not.
     *
     * @param email The email address
     * @return Whether the customer has A active request or not
     */
    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_Email(email);
    }

    /**
     * Saves A LocationEntity to the Repository.
     *
     * @param location Location chosen by customer.
     * @return The location entity.
     */
    private LocationEntity saveLocation(@Valid Location location) {
//        var locationEntity = LocationEntity.from(locationDTO);
//        return locationRepository.save(locationEntity);
        return new LocationEntity();
    }

    /**
     * Returns the trip request entity by email and status.
     *
     * @param email The email to find the trip request entity.
     * @param requestStatus The status of the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findTripRequestByEmailAndStatus(String email, String requestStatus) {
        return tripRequestRepository.findByCustomer_Email(email);
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
    public TripRequestEntity createCurrentActiveTripRequest(@Valid TripRequestBody tripRequestBody, Principal principal) throws TripRequestException {
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

//        LocationEntity startAddress = saveLocation(tripRequestBody.getStartLocation());
//        LocationEntity endAddress = saveLocation(tripRequestBody.getEndLocation());

        var tripRequestEntity = new TripRequestEntity();
//        tripRequestEntity.setStartLocation(startAddress);
//        tripRequestEntity.setEndLocation(endAddress);
//        tripRequestEntity.setCarType(tripRequestBody.getCarType());
//        tripRequestEntity.setNote(tripRequestBody.getNote());
//        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        var customerEntity = accountService.getCustomerByEmail(email);
        tripRequestEntity.setCustomer(customerEntity);

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
//        tripRequestEntity.setRequestStatus(TripRequestStatus.DELETED);

        tripRequestRepository.save(tripRequestEntity);
    }


}
