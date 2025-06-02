package com.sep.backend.trip.offer;

import com.sep.backend.trip.offer.status.*;
import org.springframework.stereotype.Service;

import com.sep.backend.account.AccountService;

import java.security.Principal;

@Service
public class TripOfferService {
    TripOfferRepository tripOfferRepository;
    AccountService accountService;

    public TripOfferService(TripOfferRepository tripOfferRepository, AccountService accountService) {
        this.tripOfferRepository = tripOfferRepository;
        this.accountService = accountService;
    }

    public String hasActiveTripOffer(Principal principal) {
        if(checkIfActiveTripOfferExists(principal.getName())) {
            return TripOfferPresenceStatus.HAS_ACTIVE_OFFER;
        }
        return TripOfferPresenceStatus.NO_ACTIVE_OFFER;
    }



    private boolean checkIfActiveTripOfferExists(String email) {
        return tripOfferRepository.existsByDriver_EmailAndStatus(email, TripOfferStatus.OPEN);
    }
}
