package com.sep.backend.triprequest;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.account.CustomerRepository;
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

    private final LocationRepository locationRepository;

    public TripRequestService(TripRequestRepository tripRequestRepository, LocationRepository locationRepository) {
        this.tripRequestRepository = tripRequestRepository;
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

    public TripRequestDTO showTripRequest(String username) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByUsername(username);
        return convertTripRequestEntityToDTO(tripRequestEntity);
    }

    //deleteFromRepository -> when customer wants to delete request
    public void deleteTripRequest(String username) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByUsername(username);
        if (Objects.equals(tripRequestEntity.getRequestStatus(), TripRequestStatus.INPROGRESS)) {
            throw new RuntimeException("Cannot delete active request");
        }
        tripRequestRepository.delete(tripRequestEntity);
    }

    //TripRequestEntity mit Constructer erstellen //TODO Change to create and delete function, not upsert
    public void createTripRequest(@Valid TripRequestDTO tripRequestDTO) {
        String username = tripRequestDTO.getUsername();
        LocationEntity startAddress = convertLocationDTOToEntity(tripRequestDTO.getStartLocation());
        LocationEntity endAddress = convertLocationDTOToEntity(tripRequestDTO.getEndLocation());
        if (existsByCustomer_Username(username)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIPREQUEST);
        }

        var tripRequestEntity = getRequestByUsername(username);
        tripRequestEntity.setStartLocation(startAddress);
        tripRequestEntity.setEndLocation(endAddress);
        tripRequestEntity.setCartype(tripRequestDTO.getCarType());
        tripRequestEntity.setNote(tripRequestDTO.getNote());
        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        tripRequestRepository.save(tripRequestEntity);

        /*if (existsByCustomer_Username(username)) {
            // update
            var tripRequestEntity = getRequestByUsername(username);
            tripRequestEntity.setStartLocation(startAddress);
            tripRequestEntity.setEndLocation(endAddress);
            tripRequestEntity.setCartype(tripRequestDTO.getCarType());
            tripRequestEntity.setNote(tripRequestDTO.getNote());
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
            tripRequestEntity.setNote(tripRequestDTO.getNote());
            tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

            tripRequestRepository.save(tripRequestEntity);
        }*/
    }
}
