package com.sep.backend.trip.offer;

import com.sep.backend.entity.DriverEntity;
import com.sep.backend.trip.offer.status.*;
import com.sep.backend.trip.offer.response.*;
import com.sep.backend.trip.offer.options.*;
import com.sep.backend.entity.TripOfferEntity;
import com.sep.backend.entity.TripHistoryEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.account.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.sep.backend.account.AccountService;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;

@Service
public class TripOfferService {
    private final DriverRepository driverRepository;
    TripOfferRepository tripOfferRepository;
    AccountService accountService;

    public TripOfferService(TripOfferRepository tripOfferRepository, AccountService accountService, DriverRepository driverRepository) {
        this.tripOfferRepository = tripOfferRepository;
        this.accountService = accountService;
        this.driverRepository = driverRepository;
    }

    public String hasActiveTripOffer(Principal principal) {
        if(checkIfActiveTripOfferExists(principal.getName())) {
            return TripOfferPresenceStatus.HAS_ACTIVE_OFFER;
        }
        return TripOfferPresenceStatus.NO_ACTIVE_OFFER;
    }

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   needs implementation from History !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public List<TripOfferResponse> getTripOfferList(Principal principal) {
//        String sort;
//        switch(sortArg) {
//            case "RATING":
//                sort = "avg(th.driverRating)";
//                break;
//            case "TOTAL_DRIVE_COUNT":
//                sort = "count(th.tripOfferId)";
//                break;
//            case "TOTAL_DRIVE_DISTANCE":
//                sort = "sum(th.distance)";
//                break;
//            default:
//                sort = "d.username";
//                break;
//        }
//        return tripOfferRepository.findTripOfferResponseByCustomer_Email(principal.getName(), sort, sortOrder);
        List<TripOfferResponse> tripOffers = new ArrayList<TripOfferResponse>();
        List<TripOfferEntity> tripOfferEntities = tripOfferRepository.findAllByTripRequest_Customer_Email(principal.getName());
        for(TripOfferEntity tripOfferEntity : tripOfferEntities) {
            DriverEntity driver = driverRepository.getById(tripOfferEntity.getDriver().getId());
            tripOffers.add(new TripOfferResponse(driver.getUsername(),
                                                 driver.getFirstName(),
                                                 driver.getLastName(),
                                                 0.0D,
                                                 0L,
                                                 0.0D));
        }
        return tripOffers;
    }

    private boolean checkIfActiveTripOfferExists(String email) {
        return tripOfferRepository.existsByDriver_EmailAndStatus(email, TripOfferStatus.OPEN);
    }
}
