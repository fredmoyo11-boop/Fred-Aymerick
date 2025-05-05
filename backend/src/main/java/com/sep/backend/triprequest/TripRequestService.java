package com.sep.backend.triprequest;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.NominatimService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;

    private final NominatimService nominatimService;
    private final CustomerRepository customerRepository;

    public TripRequestService(TripRequestRepository tripRequestRepository, NominatimService nominatimService, CustomerRepository customerRepository) {
        this.tripRequestRepository = tripRequestRepository;
        this.nominatimService = nominatimService;
        this.customerRepository = customerRepository;
    }

    public TripRequestEntity getRequestByRequestId(Long id) throws NotFoundException {
        return tripRequestRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public TripRequestEntity getRequestByUsername(String username) throws NotFoundException {
        return tripRequestRepository.findByCustomer_Username(username).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    //setRequestStatus -> when done or started set to either COMPLETED or ACTIVE -> Zyklus 2

    //deleteFromRepository -> when customer wants to delete request
    public void deleteTripRequest(String username) {
        TripRequestEntity tripRequestEntity = getRequestByUsername(username);
        if (Objects.equals(tripRequestEntity.getRequestStatus(), TripRequestStatus.ACTIVE)) {
            throw new RuntimeException("Cannot delete active request");
        }
        tripRequestRepository.delete(tripRequestEntity);
    }

    //TripRequestEntity mit Constructer erstellen
    private void createTripRequest(@Valid TripRequestDTO tripRequestDTO) {
        if (tripRequestRepository.findByCustomer_Username(tripRequestDTO.getUsername()).isPresent() &&
                Objects.equals(tripRequestRepository.findByCustomer_Username(tripRequestDTO.getUsername()).get().getRequestStatus(), TripRequestStatus.ACTIVE)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIPREQUEST);
        }

        TripRequestEntity tripRequestEntity = new TripRequestEntity();
        CustomerEntity customer = customerRepository.findByUsername(tripRequestDTO.getUsername()).orElseThrow(() -> new TripRequestException(ErrorMessages.NOT_FOUND_CUSTOMER));

        tripRequestEntity.setCustomer(customer);
        tripRequestEntity.setStartAddressCoordinates(nominatimService.getFinalAddress(tripRequestDTO.getStartAddress()));
        tripRequestEntity.setEndAddressCoordinates(nominatimService.getFinalAddress(tripRequestDTO.getEndAddress()));
        tripRequestEntity.setCartype(tripRequestEntity.getCartype());
        tripRequestEntity.setNotes(tripRequestEntity.getNotes());
        tripRequestEntity.setRequestStatus(TripRequestStatus.ACTIVE);

        tripRequestRepository.save(tripRequestEntity);
    }
}
