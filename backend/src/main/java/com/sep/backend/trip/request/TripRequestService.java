package com.sep.backend.trip.request;

import com.sep.backend.CarType;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.trip.nominatim.data.LocationRepository;
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

    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_EmailAndRequestStatus(email, TripRequestStatus.ACTIVE);
    }

    private LocationEntity saveLocation(@Valid LocationDTO locationDTO) {
        var locationEntity = LocationEntity.from(locationDTO);
        return locationRepository.save(locationEntity);
    }

    public Optional<TripRequestEntity> findTripRequestByEmailAndStatus(String email, String requestStatus) {
        return tripRequestRepository.findByCustomer_EmailAndRequestStatus(email, requestStatus);
    }

    public Optional<TripRequestEntity> findActiveTripRequestByEmail(String email) {
        return findTripRequestByEmailAndStatus(email, TripRequestStatus.ACTIVE);
    }

    @Transactional
    public TripRequestEntity createCurrentActiveTripRequest(@Valid TripRequestBody tripRequestBody, Principal principal) {
        String email = principal.getName();

        String role = accountService.getRoleByEmail(email);
        if (!Roles.CUSTOMER.equals(role)) {
            throw new TripRequestException("User must be a customer.");
        }

        if (!CarType.isValidCarType(tripRequestBody.getCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }

        // only one active trip request at a time
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIP_REQUEST);
        }

        LocationEntity startAddress = saveLocation(tripRequestBody.getStartLocation());
        LocationEntity endAddress = saveLocation(tripRequestBody.getEndLocation());

        var tripRequestEntity = new TripRequestEntity();
        tripRequestEntity.setStartLocation(startAddress);
        tripRequestEntity.setEndLocation(endAddress);
        tripRequestEntity.setCarType(tripRequestBody.getCarType());
        tripRequestEntity.setNote(tripRequestBody.getNote());
        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        var customerEntity = accountService.getCustomerByEmail(email);
        tripRequestEntity.setCustomer(customerEntity);

        return tripRequestRepository.save(tripRequestEntity);
    }

    public TripRequestDTO getCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        Optional<TripRequestEntity> tripRequestEntity = findActiveTripRequestByEmail(email);
        return tripRequestEntity.map(TripRequestDTO::from).orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
    }

    public void deleteCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        TripRequestEntity tripRequestEntity = findActiveTripRequestByEmail(email)
                .orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
        tripRequestEntity.setRequestStatus(TripRequestStatus.DELETED);

        tripRequestRepository.save(tripRequestEntity);
    }


}
