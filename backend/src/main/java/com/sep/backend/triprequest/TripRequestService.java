package com.sep.backend.triprequest;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
import com.sep.backend.triprequest.nominatim.LocationEntity;
import com.sep.backend.triprequest.nominatim.LocationRepository;
import jakarta.validation.Valid;
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

    public TripRequestEntity getRequestByEmail(String email) throws NotFoundException {
        return tripRequestRepository.findByCustomer_Email(email).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public LocationEntity getLocationById(Long id) throws NotFoundException {
        return locationRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }
    private boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_EmailAndRequestStatus(email, TripRequestStatus.ACTIVE);
    }

    //setRequestStatus -> when done or started set to either COMPLETED or ACTIVE -> Zyklus 2
    public void changeStatus(String email, String newStatus) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email);
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

    public TripRequestDTO showTripRequest(String email) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email);
        return convertTripRequestEntityToDTO(tripRequestEntity);
    }

    //deleteFromRepository -> when customer wants to delete request
    public void deleteTripRequest(String email) throws NotFoundException {
        TripRequestEntity tripRequestEntity = getRequestByEmail(email);
        if (Objects.equals(tripRequestEntity.getRequestStatus(), TripRequestStatus.INPROGRESS)) {
            throw new RuntimeException("Cannot delete active request");
        }
        tripRequestRepository.delete(tripRequestEntity);
    }

    public void createTripRequest(@Valid TripRequestDTO tripRequestDTO) {
        String email = tripRequestDTO.getEmail();
        if (!CarType.isValidCarType(tripRequestDTO.getCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIPREQUEST);
        }

        LocationEntity startAddress = convertLocationDTOToEntity(tripRequestDTO.getStartLocation());
        LocationEntity endAddress = convertLocationDTOToEntity(tripRequestDTO.getEndLocation());


        var tripRequestEntity = getRequestByEmail(email);
        tripRequestEntity.setStartLocation(startAddress);
        tripRequestEntity.setEndLocation(endAddress);
        tripRequestEntity.setCartype(tripRequestDTO.getCarType());
        tripRequestEntity.setNote(tripRequestDTO.getNote());
        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        tripRequestRepository.save(tripRequestEntity);
    }
}
