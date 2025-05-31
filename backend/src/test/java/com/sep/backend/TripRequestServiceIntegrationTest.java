package com.sep.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.account.AccountService;
import com.sep.backend.nominatim.data.LocationDTO;
import com.sep.backend.trip.request.TripRequestBody;
import com.sep.backend.trip.request.TripRequestService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.Principal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class TripRequestServiceIntegrationTest {

    @Autowired
    private TripRequestService tripRequestService;

    @Autowired
    private AccountService accountService;

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

        // Start und Ziel setzen (Koordinaten z. B. Ruhrgebiet)
        LocationDTO start = new LocationDTO();
        start.setLatitude(51.4501);
        start.setLongitude(7.0131);
        start.setDisplayName("Universität Duisburg-Essen");

        LocationDTO end = new LocationDTO();
        end.setLatitude(51.4982);
        end.setLongitude(6.8676);
        end.setDisplayName("Hbf Oberhausen");

        body.setStartLocation(start);
        body.setEndLocation(end);
        body.setStops(Collections.emptyList()); // keine Zwischenstopps

        // Principal simulieren
        Principal principal = () -> testEmail;

        // Methode ausführen
        TripRequestEntity result = tripRequestService.createCurrentActiveTripRequest(body, principal);

        // Validierungen
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(com.sep.backend.trip.request.TripRequestStatus.ACTIVE, result.getStatus());
        assertEquals("SMALL", result.getDesiredCarType());
        assertEquals("Bitte nicht rauchen.", result.getNote());
        assertEquals(testEmail, result.getCustomer().getEmail());
        assertNotNull(result.getRoute());
        assertTrue(result.getPrice() > 0);
    }
}