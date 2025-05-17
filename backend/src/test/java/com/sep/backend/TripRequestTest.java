package com.sep.backend;

import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.CarType;
import com.sep.backend.triprequest.TripRequestDTO;
import com.sep.backend.triprequest.TripRequestService;
import com.sep.backend.triprequest.TripRequestStatus;
import com.sep.backend.triprequest.nominatim.NominatimService;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
import com.sep.backend.triprequest.nominatim.LocationEntity;
import com.sep.backend.triprequest.nominatim.LocationRepository;
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
    private  TestRestTemplate restTemplate;

    @Autowired
    NominatimService nominatimService;

    @Autowired
    private  CustomerRepository customerRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TripRequestService tripRequestService;

    @BeforeEach
    public void setup() {
        CustomerEntity testCustomer = new CustomerEntity();
        testCustomer.setEmail("test@mail.com");
        testCustomer.setUsername("john_doe");
        testCustomer.setPassword("test");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setVerified(true);
        testCustomer.setBirthday("1990-01-01");
        testCustomer.setProfilePictureUrl("d.png");
        customerRepository.save(testCustomer);

        var startLocation = new LocationEntity();
        startLocation.setDisplayName("Start");
        startLocation.setLatitude(52.5);
        startLocation.setLongitude(13.4);
        var endLocation = new LocationEntity();
        endLocation.setDisplayName("End");
        endLocation.setLatitude(55.6);
        endLocation.setLongitude(11.7);
        locationRepository.save(endLocation);
        locationRepository.save(startLocation);
    }

    @AfterEach
    public void tearDown() {
        locationRepository.deleteAll();
        customerRepository.deleteAll();

    }

    @Test
    void testCreateTripRequest_createNewTripRequest() {
        /*TripRequestDTO dto = new TripRequestDTO();
        dto.setEmail("johndoe@mail.com");
        dto.setCarType(CarType.SMALL);
        dto.setNote("test note");
        dto.setStartLocation(new LocationDTO("Start", 52.5, 13.4));
        dto.setEndLocation(new LocationDTO("End", 55.6, 11.7));

        tripRequestService.createTripRequest(dto);

        TripRequestEntity request = tripRequestService.getRequestByEmail("johndoe@mail.com");
        assertEquals("john_doe", request.getCustomer().getUsername());
        assertEquals(CarType.SMALL, request.getCartype());
        assertEquals("test note", request.getNote());
        assertEquals("Start", request.getStartLocation().getDisplayName());
        assertEquals("End", request.getEndLocation().getDisplayName());
        assertEquals(52.5, request.getStartLocation().getLatitude());
        assertEquals(13.4, request.getStartLocation().getLongitude());
        assertEquals(55.6, request.getEndLocation().getLatitude());
        assertEquals(11.7, request.getEndLocation().getLongitude());
        assertEquals(TripRequestStatus.ACTIVE, request.getRequestStatus());*/
    }

    //TODO Test, if List of locations gets created when searching address

    //TODO Test, if List of locations gets created when searching coordinates

    //TODO Test, if List of locations gets created when searching Point of Interest
    @Test
    void testGetSuggestionsWithCityBerlin() throws Exception {
        List<LocationDTO> results = nominatimService.getSuggestions("Berlin");

        // Erwartung: mindestens 1 Vorschlag
        assertFalse(results.isEmpty(), "Es sollte mindestens ein Vorschlag für 'Berlin' kommen");

        // Optionale Prüfung: Ist Berlin wirklich dabei?
        boolean enthältBerlin = results.stream()
                .anyMatch(dto -> dto.getDisplayName().toLowerCase().contains("berlin"));

        assertTrue(enthältBerlin, "Mindestens ein Vorschlag sollte 'Berlin' enthalten");
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
    /*@Test
    void upsertTripRequest() {
        TripRequestDTO dto = new TripRequestDTO();
        dto.setUsername("john_doe");
        dto.setCarType(CarType.LARGE);
        dto.setNote("test note");

        LocationDTO start = new LocationDTO("Start", 52.5, 13.4);
        LocationDTO end = new LocationDTO("End", 55.6, 11.7);

        dto.setStartLocation(start);
        dto.setEndLocation(end);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TripRequestDTO> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/map/request/create",
                entity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("john_doe");
    }*/
}
