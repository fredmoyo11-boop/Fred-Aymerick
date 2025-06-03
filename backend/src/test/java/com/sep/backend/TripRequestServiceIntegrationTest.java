package com.sep.backend;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.account.DriverRepository;
import com.sep.backend.entity.*;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.route.RouteRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TripRequestServiceIntegrationTest {

    @Autowired
    private TripRequestService tripRequestService;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private  DriverRepository driverRepository;

    @Autowired
    private NominatimService nominatimService;

    private final String  email = "testoooo@gmail.com";

    @Autowired
    private CustomerRepository customerRepository;

    private final String testEmail = "hello@example.com";
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws InterruptedException {
        CustomerEntity customer = new CustomerEntity();
        customer.setEmail(testEmail);
        customer.setUsername("testuser");
        customer.setPassword("pass123");
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setVerified(true);
        customer.setBirthday("1999-01-01");
        customer.setBalance(50.0);
        customerRepository.save(customer);


        DriverEntity driver = new DriverEntity();
        driver.setEmail("aymericko@gmail.com");
        driver.setUsername("fredmoyo");
        driver.setPassword("fredmoyo");
        driver.setFirstName("Fred");
        driver.setLastName("Mustermann");
        driver.setVerified(true);
        driver.setBirthday("1999-01-01");
        driver.setBalance(50.0);
        driver.setCarType(CarTypes.DELUXE);
        driverRepository.save(driver);


        TripRequestBody body = new TripRequestBody();
        body.setDesiredCarType(CarTypes.DELUXE);
        body.setNote("Bitte nicht rauchen.");

        // Start und Ziel setzen
        Location start = new Location();
        start.setLatitude(51.4501);
        start.setLongitude(7.0131);
        start.setDisplayName("Universität Duisburg-Essen");

        Location end = new Location();
        end.setLatitude(51.4982);
        end.setLongitude(6.8676);
        end.setDisplayName("Hbf Oberhausen");

        body.setStartLocation(start);
        body.setEndLocation(end);

        // Principal simulieren
        Principal principal = () ->   testEmail ;



       tripRequestService.createCurrentActiveTripRequest(body, principal);



    }
    @AfterEach
    void DeleteAllTripRequests() {
        customerRepository.deleteAll();
        tripRequestRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Test
    void testCreateTripRequest_Success()  {
        // TripRequestBody vorbereiten
        TripRequestBody body = new TripRequestBody();
        body.setDesiredCarType("SMALL");
        body.setNote("Bitte nicht rauchen.");

        // Start und Ziel setzen
        Location start = new Location();
        start.setLatitude(51.4501);
        start.setLongitude(7.0131);
        start.setDisplayName("Universität Duisburg-Essen");

        Location end = new Location();
        end.setLatitude(51.4982);
        end.setLongitude(6.8676);
        end.setDisplayName("Hbf Oberhausen");

        body.setStartLocation(start);
        body.setEndLocation(end);

        // Principal simulieren
        Principal principal = () -> testEmail;

        // Methode ausführen
        TripRequestEntity result = tripRequestService.createCurrentActiveTripRequest(body, principal);

        // Validierungen
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(TripRequestStatus.ACTIVE, result.getStatus());
        assertEquals("SMALL", result.getDesiredCarType());
        assertEquals("Bitte nicht rauchen.", result.getNote());
        assertEquals(testEmail, result.getCustomer().getEmail());
        assertNotNull(result.getRoute());
        // assertEquals(List.of(start.getLongitude(),start.getLatitude()),result.getRoute().getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().getFirst());
        // assertEquals(List.of(end.getLongitude(),end.getLatitude()),result.getRoute().getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().getLast());
        assertTrue(result.getPrice() > 0);
    }


    @WithMockUser(username = "testuser@example.com", roles = "CUSTOMER")
    @Test
    void testGetAvailableTrips_Success() throws Exception {

        Location start = new Location();

        start.setLatitude(50.9413);
        start.setLongitude(6.9583);
        start.setDisplayName("Kölner Dom");
        String json= new ObjectMapper().writeValueAsString(start);

        String response = mockMvc.perform(post("/api/trip/request/available")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        List<AvailableTripRequestDTO> trips = mapper.readValue(
                response,
                mapper.getTypeFactory().constructCollectionType(List.class, AvailableTripRequestDTO.class)
        );
         String user = "testuser";

        assertNotNull(trips);
        assertFalse(trips.isEmpty());
        assertTrue(trips.getFirst().getDistanceInKm() > 0);
        assertEquals(user, trips.getFirst().getCustomerUsername());

    }
}