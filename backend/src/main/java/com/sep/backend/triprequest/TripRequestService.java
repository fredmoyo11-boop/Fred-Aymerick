package com.sep.backend.triprequest;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.entity.TripRequestEntity;
import org.springframework.stereotype.Service;

@Service
public class TripRequestService {

    private final TripRequestRepository tripRequestRepository;

    public TripRequestService(TripRequestRepository tripRequestRepository) {
        this.tripRequestRepository = tripRequestRepository;
    }

    public TripRequestEntity getRequestByRequestId(float id) throws NotFoundException {
        return tripRequestRepository.findByRequestID(id).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    public TripRequestEntity getRequestByUsername(String username) throws NotFoundException {
        return tripRequestRepository.findByUsername(username).orElseThrow(() -> new NotFoundException(ErrorMessages.NOT_FOUND_REQUEST));
    }

    //setRequestStatus -> when done or started set to either COMPLETED or ACTIVE

    //deleteFromRepository -> when customer wants to delete request

    //TripRequestEntity mit Constructer erstellen

}
