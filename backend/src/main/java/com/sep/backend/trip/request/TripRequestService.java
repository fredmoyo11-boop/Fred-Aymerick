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
import java.util.Objects;

@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;

    private final LocationRepository locationRepository;
    private final AccountService accountService;

    public TripRequestService(TripRequestRepository tripRequestRepository, LocationRepository locationRepository,AccountService accountService ) {
        this.tripRequestRepository = tripRequestRepository;
        this.locationRepository = locationRepository;
        this.accountService = accountService;
    }

    public TripRequestEntity getCurrentTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        return getRequestByEmailAndStatus(email, TripRequestStatus.ACTIVE);
    }

    public TripRequestEntity createCurrentTripRequest(TripRequestDTO tripRequestDTO, Principal principal) {
        String email = principal.getName();
        return createTripRequest(tripRequestDTO, email);
    }

    public TripRequestEntity getRequestByEmailAndStatus(String email, String requestStatus) throws NotFoundException {
        return tripRequestRepository.findByCustomer_EmailAndRequestStatus(email, requestStatus).getFirst();
    }

    public TripRequestEntity getRequestByEmail(String email) throws NotFoundException {
        return tripRequestRepository.findByCustomerEmail(email).orElseThrow(() -> new NotFoundException("Trip request does not exist."));
    }

    public LocationEntity getLocationById(Long id) throws NotFoundException {
        return locationRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_EmailAndRequestStatus(email, TripRequestStatus.ACTIVE);
    }

    /*//setRequestStatus -> when done or started set to either COMPLETED or ACTIVE -> Zyklus 2
    public void changeStatus(String email, String newStatus) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email, TripRequestStatus.ACTIVE);
        tripRequestEntity.setRequestStatus(newStatus);
        tripRequestRepository.save(tripRequestEntity);
    }*/

    public LocationEntity convertLocationDTOToEntity(@Valid LocationDTO locationDTO) {
        var locationEntity = LocationEntity.from(locationDTO);
        locationRepository.save(locationEntity);
        return locationEntity;
    }

    public LocationDTO convertLocationEntityToDTO(LocationEntity locationEntity) {
        return LocationDTO.from(locationEntity);
    }

    public TripRequestDTO convertTripRequestEntityToDTO(TripRequestEntity tripRequestEntity) {
        return TripRequestDTO.from(tripRequestEntity);
    }

    public TripRequestDTO showTripRequest(String email) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email);
        return convertTripRequestEntityToDTO(tripRequestEntity);
    }

    //deleteFromRepository -> when customer wants to delete request
    public void deleteTripRequest(String email) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email);
        if (Objects.equals(tripRequestEntity.getRequestStatus(), TripRequestStatus.IN_PROGRESS)) {
            throw new RuntimeException("Cannot delete active request");
        }
        tripRequestRepository.delete(tripRequestEntity);
    }

    @Transactional
    public TripRequestEntity createTripRequest(@Valid TripRequestDTO tripRequestDTO, String email) {
        if (!CarType.isValidCarType(tripRequestDTO.getCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIPREQUEST);
        }

        LocationEntity startAddress = convertLocationDTOToEntity(tripRequestDTO.getStartLocation());
        LocationEntity endAddress = convertLocationDTOToEntity(tripRequestDTO.getEndLocation());

        var tripRequestEntity = new TripRequestEntity();
        tripRequestEntity.setStartLocation(startAddress);
        tripRequestEntity.setEndLocation(endAddress);
        tripRequestEntity.setCarType(tripRequestDTO.getCarType());
        tripRequestEntity.setNote(tripRequestDTO.getNote());
        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        String role = accountService.getRoleByEmail(email);
        if (!Roles.CUSTOMER.equals(role)) {
            throw new RuntimeException("User must be a customer.");
        }
        var customerEntity = accountService.getCustomerByEmail(email);
        tripRequestEntity.setCustomer(customerEntity);
        return tripRequestRepository.save(tripRequestEntity);
    }
}
