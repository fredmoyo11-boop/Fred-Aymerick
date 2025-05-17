package com.sep.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Java6Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TripRequestTest {
    /*
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
    } */
}
