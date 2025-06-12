package com.sep.backend.trip.offer;

import static org.junit.jupiter.api.Assertions.*;

import com.sep.backend.trip.offer.*;
import com.sep.backend.trip.offer.options.*;
import com.sep.backend.trip.offer.response.*;
import com.sep.backend.trip.offer.status.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TripOfferServiceTest {

    @Autowired
    private TripOfferRepository tripOfferRepository;

    @Autowired
    private TripOfferService tripOfferService;

    @Test
    public void hasActiveTripOfferTest() {
        
    }

    @Test
    public void createNewTripOfferTest() {

    }

    @Test
    public void acceptOfferTest() {

    }

    @Test
    public void declineOfferTest() {

    }

    @Test
    public void withdrawOfferTest() {

    }

//    @Test
//    public void setStatusTest() {
//
//    }

    @Test
    public void getTripOfferListTest() {

    }

//    @Test
//    public void checkIfActiveTripOfferExistsTest() {
//
//    }

    @Test
    public void isPartOfTripTest() {

    }

    @Test
    public void findRoleOfTripByEmailTest() {

    }

    @Test
    public void findRoleOfTripByPrincipalTest() {

    }

    @Test
    public void completeTripOfferTest() {

    }
}