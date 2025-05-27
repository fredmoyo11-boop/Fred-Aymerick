package com.sep.backend;

import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import com.sep.backend.trip.nominatim.data.LocationRepository;
import com.sep.backend.trip.request.AvailableTripRequestDTO;
import com.sep.backend.trip.request.TripRequestRepository;
import com.sep.backend.trip.request.TripRequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {


    @Autowired
    private TripRequestService tripRequestService;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    void shouldReturnDistanceFromDriverToRequestStart() {
        // 1. Fahrerposition
        LocationDTO fahrerOrt = new LocationDTO();
        fahrerOrt.setLatitude(51.4341);   // Duisburg
        fahrerOrt.setLongitude(6.7623);
        fahrerOrt.setDisplayName("Duisburg");

        // 2. TripRequest vorbereiten (mit LocationEntity + Customer)
        LocationEntity start = new LocationEntity("Zoo Duisburg", 51.4677, 6.7876);
        LocationEntity ziel = new LocationEntity("Essen Hbf", 51.4508, 7.0131);
        locationRepository.saveAll(List.of(start, ziel));

        CustomerEntity kunde = new CustomerEntity();
        kunde.setUsername("fredTest");
        kunde.setEmail("leodian@yahoo.fr");
        kunde.setBirthday("2000-01-01");
        kunde.setVerified(true);
        kunde.setFirstName("Fred");
        kunde.setLastName("Test");
        kunde.setTotalNumberOfRides(0);
        kunde.setPassword("fredTest");
        customerRepository.save(kunde);

        TripRequestEntity request = new TripRequestEntity();
        request.setStartLocation(start);
        request.setEndLocation(ziel);
        request.setCustomer(kunde);
        request.setRequestStatus("ACTIVE");
        request.setCarType("DELUXE");

        tripRequestRepository.save(request);

        // 3. Testaufruf
        List<AvailableTripRequestDTO> result = tripRequestService.getAvailableRequests(fahrerOrt);

        // 4. Prüfung
        Assertions.assertFalse(result.isEmpty());
        AvailableTripRequestDTO dto = result.getFirst();
        System.out.println("Gefundene Distanz: " + dto.getDistance() + " km");

        assertTrue(dto.getDistance() > 0);
        assertEquals("fredTest", dto.getUsername());
    }
}
