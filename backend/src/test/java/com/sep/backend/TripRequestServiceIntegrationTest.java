package com.sep.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.location.Location;
import com.sep.backend.trip.request.TripRequestBody;
import com.sep.backend.trip.request.TripRequestService;
import com.sep.backend.trip.request.TripRequestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.Principal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class TripRequestServiceIntegrationTest {

    @Autowired
    private TripRequestService tripRequestService;

    @Autowired
    private CustomerRepository customerRepository;

    private final String testEmail = "testuser@example.com";

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testCreateTripRequest_Success() throws JsonProcessingException {
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
}