package com.sep.backend.trip.offer;

import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.entity.*;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.trip.request.TripRequestRepository;
import com.sep.backend.route.RouteRepository;
import com.sep.backend.CarTypes;
import com.sep.backend.trip.request.TripRequestStatus;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.Principal;
import java.time.LocalDateTime;


@SpringBootTest
@ActiveProfiles("test")
public class TripOfferServiceTest {

    @Mock
    private Principal principal;

    @Autowired
    private TripOfferRepository tripOfferRepository;

    @Autowired
    private TripOfferService tripOfferService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @BeforeEach
    public void setup() {
        tripOfferRepository.deleteAll();
        tripOfferRepository.flush();

        driverRepository.deleteAll();
        driverRepository.flush();

        tripRequestRepository.deleteAll();
        tripRequestRepository.flush();

        routeRepository.deleteAll();
        routeRepository.flush();

        customerRepository.deleteAll();
        customerRepository.flush();

        CustomerEntity customer1 = new CustomerEntity();
        customer1.setFirstName("Mario");
        customer1.setLastName("Mario");
        customer1.setEmail("mario@gmail.com");
        customer1.setPassword("Mario123");
        customer1.setUsername("Mario");
        customer1.setBirthday("1981-07-09");
        customer1.setVerified(true);
        customer1.setBalance(100.0);
        customerRepository.save(customer1);

        CustomerEntity customer2 = new CustomerEntity();
        customer2.setFirstName("Luigi");
        customer2.setLastName("Mario");
        customer2.setEmail("luigi@gmail.com");
        customer2.setPassword("Luigi123");
        customer2.setUsername("Luigi");
        customer2.setBirthday("1983-03-14");
        customer2.setVerified(true);
        customer2.setBalance(100.0);
        customerRepository.save(customer2);

        RouteEntity route1 = new RouteEntity();
        route1.setGeoJSON(new ORSFeatureCollection());
        routeRepository.save(route1);

        RouteEntity route2 = new RouteEntity();
        route2.setGeoJSON(new ORSFeatureCollection());
        routeRepository.save(route2);

        RouteEntity route3 = new RouteEntity();
        route3.setGeoJSON(new ORSFeatureCollection());
        routeRepository.save(route3);

        TripRequestEntity tripRequest1 = new TripRequestEntity();
        tripRequest1.setCustomer(customer1);
        tripRequest1.setRoute(route1);
        tripRequest1.setRequestTime(LocalDateTime.now());
        tripRequest1.setCarType(CarTypes.MEDIUM);
        tripRequest1.setStatus(TripRequestStatus.ACTIVE);
        tripRequest1.setPrice(1.0D);
        tripRequest1.setNote("This is a test");
        tripRequestRepository.save(tripRequest1);

        TripRequestEntity tripRequest2 = new TripRequestEntity();
        tripRequest2.setCustomer(customer2);
        tripRequest2.setRoute(route2);
        tripRequest2.setRequestTime(LocalDateTime.now());
        tripRequest2.setCarType(CarTypes.SMALL);
        tripRequest2.setStatus(TripRequestStatus.ACTIVE);
        tripRequest2.setPrice(1.5D);
        tripRequest2.setNote("This is a test");
        tripRequestRepository.save(tripRequest2);

        TripRequestEntity tripRequest3 = new TripRequestEntity();
        tripRequest3.setCustomer(customer1);
        tripRequest3.setRoute(route3);
        tripRequest3.setRequestTime(LocalDateTime.now());
        tripRequest3.setCarType(CarTypes.SMALL);
        tripRequest3.setStatus(TripRequestStatus.COMPLETED);
        tripRequest3.setPrice(1.5D);
        tripRequest3.setNote("This is a test");
        tripRequestRepository.save(tripRequest3);

        DriverEntity driver1 = new DriverEntity();
        driver1.setFirstName("John");
        driver1.setLastName("Doe");
        driver1.setEmail("johndoe@gmail.com");
        driver1.setPassword("password");
        driver1.setUsername("johndoe");
        driver1.setBirthday("2000-01-01");
        driver1.setVerified(true);
        driver1.setBalance(100.0);
        driverRepository.save(driver1);

        DriverEntity driver2 = new DriverEntity();
        driver2.setFirstName("Jane");
        driver2.setLastName("Doe");
        driver2.setEmail("janedoe@gmail.com");
        driver2.setPassword("password");
        driver2.setUsername("janedoe");
        driver2.setBirthday("2000-01-01");
        driver2.setVerified(true);
        driver2.setBalance(100.0);
        driverRepository.save(driver2);

        TripOfferEntity tripOffer1 = new TripOfferEntity();
        tripOffer1.setDriver(driver1);
        tripOffer1.setTripRequest(tripRequest1);
        tripOffer1.setStatus(TripOfferStatus.PENDING);
        tripOfferRepository.save(tripOffer1);

        TripOfferEntity tripOffer2 = new TripOfferEntity();
        tripOffer2.setDriver(driver2);
        tripOffer2.setTripRequest(tripRequest2);
        tripOffer2.setStatus(TripOfferStatus.REVOKED);
        tripOfferRepository.save(tripOffer2);

        TripOfferEntity tripOffer3 = new TripOfferEntity();
        tripOffer3.setDriver(driver2);
        tripOffer3.setTripRequest(tripRequest3);
        tripOffer3.setStatus(TripOfferStatus.REJECTED);
        tripOfferRepository.save(tripOffer3);
    }

    @Test
    public void createNewTripOfferTest() {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("janedoe@gmail.com");

        //tripOfferService.createNewTripOffer()
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