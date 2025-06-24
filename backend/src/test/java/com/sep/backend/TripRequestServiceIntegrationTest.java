package com.sep.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sep.backend.trip.history.TripHistoryDTO;
import com.sep.backend.trip.history.TripHistoryRepository;
import com.sep.backend.trip.history.TripHistoryService;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.*;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.ors.ORSService;
import com.sep.backend.route.Coordinate;
import com.sep.backend.trip.offer.TripOfferRepository;
import com.sep.backend.trip.request.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TripRequestServiceIntegrationTest {

    @Autowired
    private TripRequestService tripRequestService;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TripHistoryService tripHistoryService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ORSService orsService;

    @Autowired
    private TripHistoryRepository tripHistoryRepository;

    @Autowired
    private TripOfferRepository tripOfferRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final String testEmail = "hellokitit@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NominatimService nominatimService;

    @BeforeEach
    void setUp() throws InterruptedException, JsonProcessingException {

        CustomerEntity customer = new CustomerEntity();
        customer.setEmail(testEmail);
        customer.setUsername("userrrr");
        customer.setPassword("pass123");
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setVerified(true);
        customer.setBirthday("1999-01-01");
        customer.setBalance(50.0);
        customerRepository.save(customer);


        DriverEntity driver = new DriverEntity();
        driver.setEmail("aymerickooo@gmail.com");
        driver.setUsername("freddioii");
        driver.setPassword("fredmoyo");
        driver.setFirstName("Fred");
        driver.setLastName("Mustermann");
        driver.setVerified(true);
        driver.setBirthday("1999-01-01");
        driver.setBalance(50.0);
        driver.setCarType(CarTypes.DELUXE);
        driverRepository.save(driver);


        TripRequestBody body = new TripRequestBody();
        body.setCarType(CarTypes.DELUXE);
        body.setNote("Bitte nicht rauchen.");

        // Start und Ziel setzen
        Location start = new Location();
        var startCoordinate = new Coordinate();
        startCoordinate.setLatitude(51.4501);
        startCoordinate.setLongitude(7.0131);
        start.setCoordinate(startCoordinate);
        start.setDisplayName("Universität Duisburg-Essen");
        start.setGeoJSON(nominatimService.reverse(String.valueOf(start.getCoordinate().getLatitude()), String.valueOf(start.getCoordinate().getLongitude())).getFeatures().getFirst());
        Location end = new Location();
        var endCoordinate = new Coordinate();
        endCoordinate.setLatitude(51.4982);
        endCoordinate.setLongitude(6.8676);
        end.setCoordinate(endCoordinate);
        end.setDisplayName("Hbf Oberhausen");
        end.setGeoJSON(nominatimService.reverse(String.valueOf(end.getCoordinate().getLatitude()), String.valueOf(end.getCoordinate().getLongitude())).getFeatures().getFirst());


        body.setLocations(List.of(start, end));
        body.setGeojson(orsService.getRouteDirections(List.of(Coordinate.from(start), Coordinate.from(end))));

        var trip = tripRequestService.createCurrentActiveTripRequest(body, () -> testEmail);


        TripOfferEntity tripOfferEntity = new TripOfferEntity();
        tripOfferEntity.setTripRequest(trip);
        tripOfferEntity.setDriver(driver);
        tripOfferEntity.setStatus(TripRequestStatus.ACTIVE);
        var offer = tripOfferRepository.save(tripOfferEntity);

        var history = tripHistoryService.saveTripHistory(
                offer,
                trip.getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDistance(),
                (int) trip.getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDuration(),
                3,
                2
        );

    }

    @AfterEach
    void DeleteAllTripRequests() {

        tripHistoryRepository.deleteAll();
        tripOfferRepository.deleteAll();
        tripRequestRepository.deleteAll();
        locationRepository.deleteAll();
        driverRepository.deleteAll();
        customerRepository.deleteAll();


    }

    @Test
    @WithMockUser(username = testEmail, roles = "CUSTOMER")
    void testCreateTripRequest_Success() throws Exception {

        Location start = new Location();
        var startCoordinate = new Coordinate();
        startCoordinate.setLatitude(51.4501);
        startCoordinate.setLongitude(7.0131);
        start.setCoordinate(startCoordinate);
        start.setDisplayName("Universität Duisburg-Essen");
        start.setGeoJSON(nominatimService.reverse("51.4501", "7.0131").getFeatures().getFirst());

        Location end = new Location();
        var endCoordinate = new Coordinate();
        endCoordinate.setLatitude(51.4982);
        endCoordinate.setLongitude(6.8676);
        end.setCoordinate(endCoordinate);
        end.setDisplayName("Hbf Oberhausen");
        end.setGeoJSON(nominatimService.reverse("51.4982", "6.8676").getFeatures().getFirst());

        TripRequestBody body = new TripRequestBody();
        body.setCarType("SMALL");
        body.setNote("Bitte nicht rauchen.");
        body.setLocations(List.of(start, end));
        body.setGeojson(orsService.getRouteDirections(List.of(Coordinate.from(start), Coordinate.from(end))));

        Principal principal = () -> testEmail;

        if (tripRequestService.existsActiveTripRequest(testEmail)) {

            tripRequestService.deleteCurrentActiveTripRequest(principal);

        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        String result = mockMvc.perform(post("/api/trip/request/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String user = "userrrr";

        TripRequestDTO trips = mapper.readValue(result, TripRequestDTO.class);
        assertNotNull(trips);
        assertEquals(TripRequestStatus.ACTIVE, trips.getStatus());
        assertEquals("SMALL", trips.getCarType());
        assertEquals("Bitte nicht rauchen.", trips.getNote());
        assertNotNull(trips.getRoute());
        // assertEquals(List.of(start.getLongitude(),start.getLatitude()),result.getRoute().getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().getFirst());
        // assertEquals(List.of(end.getLongitude(),end.getLatitude()),result.getRoute().getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().getLast());
        assertTrue(trips.getPrice() > 0);
    }


    @WithMockUser(username = "testuser@example.com", roles = "CUSTOMER")
    @Test
    void testGetAvailableTrips_Success() throws Exception {

        Location start = new Location();
        var startCoordinate = new Coordinate();
        startCoordinate.setLatitude(50.9413);
        startCoordinate.setLongitude(6.9583);
        start.setCoordinate(startCoordinate);
        start.setDisplayName("Kölner Dom");
        start.setGeoJSON(nominatimService.reverse("50.9413", "6.9583").getFeatures().getFirst());

        String string = new ObjectMapper().writeValueAsString(start);

        String response = mockMvc.perform(post("/api/trip/request/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(string))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        List<AvailableTripRequestDTO> trips = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, AvailableTripRequestDTO.class));

        String user = "userrrr";

        assertNotNull(trips);
        assertFalse(trips.isEmpty());
        assertTrue(trips.getFirst().getDistanceInKm() > 0);
        assertEquals(user, trips.getFirst().getCustomerUsername());

    }

    @Test
    @WithMockUser(username = "aymerickooo@gmail.com", roles = Roles.DRIVER)
    void testGetTripHistory_Success() throws Exception {

        String response = mockMvc.perform(get("/api/trip/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        List<TripHistoryDTO> history = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, TripHistoryDTO.class));

        assertNotNull(history);
        assertFalse(history.isEmpty());
        TripHistoryDTO historyDTO = history.getFirst();
        assertNotNull(historyDTO);
        assertEquals("userrrr", history.getFirst().getCustomerUsername());
        assertEquals("freddioii", history.getFirst().getDriverUsername());
        assertEquals(2, history.getFirst().getCustomerRating());
        assertEquals(3, history.getFirst().getDriverRating());


    }
}