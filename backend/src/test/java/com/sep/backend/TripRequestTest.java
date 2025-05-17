package com.sep.backend;

import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.nominatim.NominatimService;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import com.sep.backend.trip.nominatim.data.LocationRepository;
import com.sep.backend.trip.request.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TripRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    NominatimService nominatimService;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TripRequestService tripRequestService;
    @Autowired
    private TripRequestRepository tripRequestRepository;

    @BeforeEach
    public void setup() {
        tripRequestRepository.deleteAll();
        tripRequestRepository.flush();
        locationRepository.deleteAll();
        customerRepository.deleteAll();

        CustomerEntity testCustomer = new CustomerEntity();
        testCustomer.setEmail("johndoe@mail.com");
        testCustomer.setUsername("john_doe");
        testCustomer.setPassword("test");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setVerified(true);
        testCustomer.setBirthday("1990-01-01");
        testCustomer.setProfilePictureUrl("d.png");
        if (!customerRepository.existsByEmail("johndoe@mail.com")) {
            customerRepository.save(testCustomer);
        }


        CustomerEntity testCustomer2 = new CustomerEntity();
        testCustomer2.setEmail("karl@mail.com");
        testCustomer2.setUsername("karl");
        testCustomer2.setPassword("KARL");
        testCustomer2.setFirstName("Karl");
        testCustomer2.setLastName("Kohl");
        testCustomer2.setVerified(true);
        testCustomer2.setBirthday("2004-05-21");
        testCustomer2.setProfilePictureUrl("k.png");
        customerRepository.save(testCustomer2);

        var startLocation = new LocationEntity();
        startLocation.setDisplay_name("Gustav-Heinemann-Gesamtschule");
        startLocation.setLat(51.44833475);
        startLocation.setLon(6.903738267025127);
        var endLocation = new LocationEntity();
        endLocation.setDisplay_name("Edeka Kels");
        endLocation.setLat(51.42810685);
        endLocation.setLon(6.93780940053893);
        locationRepository.save(endLocation);
        locationRepository.save(startLocation);

        TripRequestEntity tripRequest = new TripRequestEntity();
        tripRequest.setCustomer(testCustomer);
        tripRequest.setStartLocation(startLocation);
        tripRequest.setEndLocation(endLocation);
        tripRequest.setNote("Testnote");
        tripRequest.setRequestStatus(TripRequestStatus.ACTIVE);
        tripRequest.setCartype(CarType.MEDIUM);
        tripRequestRepository.save(tripRequest);

    }

    @Test
    void testGetSuggestionsWithCityBerlin() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("Berlin");

        // Erwartung: mindestens 1 Vorschlag
        assertFalse(results.isEmpty(), "Es sollte mindestens ein Vorschlag für 'Berlin' kommen");

        // Optionale Prüfung: Ist Berlin wirklich dabei?
        boolean containsBerlin = results.stream()
                .anyMatch(dto -> dto.getDisplay_name().toLowerCase().contains("berlin"));

        assertTrue(containsBerlin, "Mindestens ein Vorschlag sollte 'Berlin' enthalten");
    }

    @Test
    void testGetSuggestionsWithGibberishShouldReturnNothing() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("asdfghjkl");

        // Erwartung: keine Vorschläge
        assertTrue(results.isEmpty(), "Für Unsinnige Eingabe sollte keine Adresse zurückkommen");
    }

    @Test
    void testGetSuggestionsWithPartialInputReturnsMultipleResults() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("Ber");

        // Erwartung: mehrere Vorschläge
        assertTrue(results.size() >= 3, "Für Teil-Eingabe 'Ber' sollte es mehrere Ergebnisse geben");
    }

    @Test
    void testGetSuggestionsWithEmptyInputReturnsNothing() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("");

        //Erwartung: Leere Liste
        assertThat(results).isEmpty();
    }

    @Test
    void testGetSuggestionsWithCoordinates() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("51.42810685,6.93780940053893");
        boolean containsEdeka = results.stream()
                .anyMatch(dto -> dto.getDisplay_name().toLowerCase().contains("edeka"));
        //Erwartung: Edeka gefunden durch Koordinaten
        assertTrue(containsEdeka, "Edeka wurde gefunden");
    }

    @Test
    void testGetSuggestionsWithFalseCoordinates() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("51.428106856.937809400538931"); //Falsches Format
        List<LocationDTO> results2 = nominatimService.getSuggestions("49.538957458382654, -21.244806274588246"); //Im Meer

        //Erwartung: Leere Liste
        assertThat(results).isEmpty();
        //Erwartung: Leere Liste
        assertThat(results2).isEmpty();
    }

    @Test
    void testCreateTripRequestSuccessfully() {
        LocationDTO startLocation = new LocationDTO();
        startLocation.setDisplay_name("Freilichtbühne Mülheim an der Ruhr");
        startLocation.setLat(51.4222987);
        startLocation.setLon(6.8856315);
        LocationDTO endLocation = new LocationDTO();
        endLocation.setDisplay_name("Edeka Kels");
        endLocation.setLat(51.42810685);
        endLocation.setLon(6.93780940053893);

        TripRequestDTO  dto = new TripRequestDTO();
        dto.setEmail("karl@mail.com");
        dto.setStartLocation(startLocation);
        dto.setEndLocation(endLocation);
        dto.setStatus(TripRequestStatus.ACTIVE);
        dto.setCarType(CarType.DELUXE);
        dto.setNote("Testnote");

        tripRequestService.createTripRequest(dto);

        assertTrue(tripRequestRepository.existsByCustomer_EmailAndRequestStatus(dto.getEmail(), TripRequestStatus.ACTIVE));
    }

    @Test
    void testDeleteTripRequestSuccessfully() {
        tripRequestService.deleteTripRequest("johndoe@mail.com");

        //Erwartung: TripRequest erfolgreich aus Repository gelöscht
        assertFalse(tripRequestService.existsActiveTripRequest("johndoe@mail.com"));
    }

    /*@Test
    void testDeleteTripRequestFailed() throws NotFoundException {
        //Erwartung: TripRequest nicht gefunden in Repository -> NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripRequestService.deleteTripRequest("maxmustermann@mail.com"));
        assertEquals("No TripRequest found for customer with email: maxmustermann@mail.com", exception.getMessage());
    }*/
}
