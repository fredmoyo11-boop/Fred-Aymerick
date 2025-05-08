package com.sep.backend.triprequest;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.LocationDTO;
import com.sep.backend.triprequest.nominatim.LocationEntity;
import com.sep.backend.triprequest.nominatim.LocationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;

    public TripRequestService(TripRequestRepository tripRequestRepository,CustomerRepository customerRepository
            , LocationRepository locationRepository) {
        this.tripRequestRepository = tripRequestRepository;
        this.customerRepository = customerRepository;
        this.locationRepository = locationRepository;
    }

    public TripRequestEntity getRequestById(Long id) throws NotFoundException {
        return tripRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public TripRequestEntity getRequestByUsername(String username) throws NotFoundException {
        return tripRequestRepository.findByCustomer_Username(username).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public LocationEntity getLocationById(Long id) throws NotFoundException {
        return locationRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    private boolean existsByCustomer_Username(@NotNull String username) {
        return tripRequestRepository.existsByCustomer_Username(username);
    }

    //setRequestStatus -> when done or started set to either COMPLETED or ACTIVE -> Zyklus 2
    public void changeStatus(String username, String newStatus) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByUsername(username);
        tripRequestEntity.setRequestStatus(newStatus);
        tripRequestRepository.save(tripRequestEntity);
    }


    //deleteFromRepository -> when customer wants to delete request
    public void deleteTripRequest(String username) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByUsername(username);
        if (Objects.equals(tripRequestEntity.getRequestStatus(), TripRequestStatus.INPROGRESS)) {
            throw new RuntimeException("Cannot delete active request");
        }
        tripRequestRepository.delete(tripRequestEntity);
    }

    public void saveLocation(@NotNull LocationEntity location) {
        locationRepository.save(location);
    }

    public LocationEntity convertDTOToEntity(@Valid LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDisplayName(locationDTO.getDisplayName());
        locationEntity.setLatitude(locationDTO.getLatitude());
        locationEntity.setLongitude(locationDTO.getLongitude());
        locationRepository.save(locationEntity);
        return locationEntity;
    }

    //TripRequestEntity mit Constructer erstellen
    public void upsertTripRequest(@Valid TripRequestDTO tripRequestDTO) {
        String username = tripRequestDTO.getUsername();
        LocationEntity startAddress = convertDTOToEntity(tripRequestDTO.getStartLocation());
        LocationEntity endAddress = convertDTOToEntity(tripRequestDTO.getEndLocation());
        if (existsByCustomer_Username(username)) {
            // update
            var tripRequestEntity = getRequestByUsername(username);
            tripRequestEntity.setStartLocation(startAddress);
            tripRequestEntity.setEndLocation(endAddress);
            tripRequestEntity.setCartype(tripRequestDTO.getCarType());
            tripRequestEntity.setNotes(tripRequestDTO.getNote());
            tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

            tripRequestRepository.save(tripRequestEntity);
        } else {
            // create new
            TripRequestEntity tripRequestEntity = new TripRequestEntity();
            CustomerEntity customer = customerRepository.findByUsername(tripRequestDTO.getUsername()).orElseThrow(() -> new TripRequestException(ErrorMessages.NOT_FOUND_CUSTOMER));

            tripRequestEntity.setCustomer(customer);
            tripRequestEntity.setStartLocation(startAddress);
            tripRequestEntity.setEndLocation(endAddress);
            tripRequestEntity.setCartype(tripRequestDTO.getCarType());
            tripRequestEntity.setNotes(tripRequestDTO.getNote());
            tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

            tripRequestRepository.save(tripRequestEntity);
        }
    }
}
