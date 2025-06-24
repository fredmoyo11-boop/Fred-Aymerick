package com.sep.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.account.CustomerRepository;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.ORSService;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.route.Coordinate;
import com.sep.backend.route.RouteRepository;
import com.sep.backend.trip.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class TripRequestTest {

//    @Autowired
//    ResourceLoader resourceLoader;
//
//    @Mock
//    private Principal principal;
//
//    @Autowired
//    NominatimService nominatimService;
//
//    @Autowired
//    ORSService orsService;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//    @Autowired
//    private LocationRepository locationRepository;
//    @Autowired
//    private TripRequestService tripRequestService;
//    @Autowired
//    private TripRequestRepository tripRequestRepository;
//    @Autowired
//    private RouteRepository routeRepository;
//
//    @BeforeEach
//    public void setup() throws IOException {
//        tripRequestRepository.deleteAll();
//        tripRequestRepository.flush();
//        locationRepository.deleteAll();
//        routeRepository.deleteAll();
//        customerRepository.deleteAll();
//
//        CustomerEntity testCustomer = new CustomerEntity(); //Creates customer entity with a trip request
//        testCustomer.setEmail("johndoe@mail.com");
//        testCustomer.setUsername("john_doe");
//        testCustomer.setPassword("test");
//        testCustomer.setFirstName("John");
//        testCustomer.setLastName("Doe");
//        testCustomer.setVerified(true);
//        testCustomer.setBirthday("1990-01-01");
//        testCustomer.setProfilePictureUrl("d.png");
//        if (!customerRepository.existsByEmailIgnoreCase("johndoe@mail.com")) {
//            customerRepository.save(testCustomer);
//        }
//
//
//        CustomerEntity testCustomer2 = new CustomerEntity(); //Creates customer entity with no trip request
//        testCustomer2.setEmail("karl@mail.com");
//        testCustomer2.setUsername("karl");
//        testCustomer2.setPassword("KARL");
//        testCustomer2.setFirstName("Karl");
//        testCustomer2.setLastName("Kohl");
//        testCustomer2.setVerified(true);
//        testCustomer2.setBirthday("2004-05-21");
//        testCustomer2.setProfilePictureUrl("k.png");
//        customerRepository.save(testCustomer2);
//
//        //var mockJson = loadORSJsonMock();
//        //Mockito.when(orsService.getRouteDirections(Mockito.any())).thenReturn(mockJson);
//
////        var startLocation = new LocationEntity();
////        startLocation.setDisplay_name("Gustav-Heinemann-Gesamtschule");
////        startLocation.setLat(51.44833475);
////        startLocation.setLon(6.903738267025127);
////        var endLocation = new LocationEntity();
////        endLocation.setDisplay_name("Edeka Kels");
////        endLocation.setLat(51.42810685);
////        endLocation.setLon(6.93780940053893);
////        locationRepository.save(endLocation);
////        locationRepository.save(startLocation);
////
////        TripRequestEntity tripRequest = new TripRequestEntity(); //Creates full trip request with customer johndoe@mail.com
////        tripRequest.setCustomer(testCustomer);
////        tripRequest.setStartLocation(startLocation);
////        tripRequest.setEndLocation(endLocation);
////        tripRequest.setNote("Testnote");
////        tripRequest.setRequestStatus(TripRequestStatus.ACTIVE);
////        tripRequest.setCarType(CarType.MEDIUM);
////        tripRequestRepository.save(tripRequest);
//
//    }
//
//    @Test
//    void testGetSuggestionsWithCityBerlin() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("Berlin");
////
////        // Expected: minimum 1 location in list.
////        assertFalse(results.isEmpty(), "Es sollte mindestens ein Vorschlag für 'Berlin' kommen");
////
////        // Optional test: Is Berlin in the list?
////        boolean containsBerlin = results.stream()
////                .anyMatch(dto -> dto.getDisplayName().toLowerCase().contains("berlin"));
////
////        assertTrue(containsBerlin, "Mindestens ein Vorschlag sollte 'Berlin' enthalten");
//    }
//
//    @Test
//    void testGetSuggestionsWithGibberishShouldReturnNothing() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("asdfghjkl");
////
////        // Expected: no locations in list.
////        assertTrue(results.isEmpty(), "Für Unsinnige Eingabe sollte keine Adresse zurückkommen");
//    }
//
//    @Test
//    void testGetSuggestionsWithPartialInputReturnsMultipleResults() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("Ber");
////
////        // Expected: multiple locations in list.
////        assertTrue(results.size() >= 3, "Für Teil-Eingabe 'Ber' sollte es mehrere Ergebnisse geben");
//    }
//
//    @Test
//    void testGetSuggestionsWithEmptyInputReturnsNothing() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("");
////
////        //Expected: No locations in list.
////        assertThat(results).isEmpty();
//    }
//
//    @Test
//    void testGetSuggestionsWithCoordinates() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("51.42810685,6.93780940053893");
////        boolean containsEdeka = results.stream()
////                .anyMatch(dto -> dto.getDisplayName().toLowerCase().contains("edeka"));
////        //Expected: Found edeka with coordinates.
////        assertTrue(containsEdeka, "Edeka wurde gefunden");
//    }
//
//    @Test
//    void testGetSuggestionsWithFalseCoordinates() throws Exception {
////        List<LocationDTO> results = nominatimService.searchLocations("51.428106856.937809400538931"); //Falsches Format
////        List<LocationDTO> results2 = nominatimService.searchLocations("49.538957458382654, -21.244806274588246"); //Im Meer
////
////        //Expected: No locations in list.
////        assertThat(results).isEmpty();
////        //Expected: No locations in list.
////        assertThat(results2).isEmpty();
//    }
//
//    @Test
//    void testCreateTripRequestSuccessfully() throws IOException {
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("karl@mail.com");
////
////        var start = nominatimService.search("Mülheim an der Ruhr");
////        var end = nominatimService.search("Edeka Kels Muelheim");
////
//////        var start = nominatimService.reverse("51.4222987", "6.8856315");//Freilichtbühne Mülheim an der Ruhr
//////        var end = nominatimService.reverse("51.42810685", "6.93780940053893"); //Edeka Kels
//////        var start_location = Location.from(start.getFeatures().getFirst());
//////        var end_location = Location.from(end.getFeatures().getFirst());
//////        var start_coordinates = Coordinate.from(start_location);
//////        System.out.println(start_coordinates.getLatitude() + " " + start_coordinates.getLongitude());
//////        var end_coordinates = Coordinate.from(end_location);
//////        System.out.println(end_coordinates.getLatitude() + " " + end_coordinates.getLongitude());
////
////        var start_location = start.getFirst();
////        System.out.println(start_location.getDisplayName());
////        var end_location = end.getFirst();
////        System.out.println(end_location.getDisplayName());
////        var start_coordinates = start_location.getCoordinate();
////        System.out.println("Lon: " + start_coordinates.getLongitude() + " Lat: " + start_coordinates.getLatitude());
////        var end_coordinates = end_location.getCoordinate();
////        System.out.println("Lon: " + end_coordinates.getLongitude() + " Lat: " + end_coordinates.getLatitude());
////
////        var geojson = orsService.getRouteDirections(List.of(start_coordinates, end_coordinates));
////        System.out.println(geojson);
////        double lat_start = geojson.getFeatures().getFirst().getGeometry().getCoordinates().getFirst().getFirst();
////        double lon_start = geojson.getFeatures().getFirst().getGeometry().getCoordinates().getFirst().getLast();
////        double lat_end = geojson.getFeatures().getFirst().getGeometry().getCoordinates().getLast().getFirst();
////        double lon_end = geojson.getFeatures().getFirst().getGeometry().getCoordinates().getLast().getLast();
////        System.out.println("Start Koordinaten: " + lat_start + " " + lon_start);
////        System.out.println("End Koordinaten: " + lat_end + " " + lon_end);
////
////        TripRequestBody body = new TripRequestBody();
////        body.setCarType(CarTypes.DELUXE);
////        body.setNote("Testnote");
////        body.setLocations(List.of(start_location, end_location));
////        System.out.println(start_location.getDisplayName() + " " + end_location.getDisplayName());
////        body.setGeojson(geojson);
////
////
////        tripRequestService.createCurrentTripRequest(body, principal);
////
////        double  distance =body.getGeojson().getFeatures().getFirst().getProperties().getSummary().getDistance();
////        double time = body.getGeojson().getFeatures().getFirst().getProperties().getSummary().getDuration();
////        System.out.println("Distance + Time " + distance + " " + time);
////
////
////        //Expected: Successfully creates trip request.
////        assertTrue(tripRequestRepository.existsByCustomer_EmailAndStatus("karl@mail.com" ,TripRequestStatus.ACTIVE));
//    }
//
//    @Test
//    void testCreateSecondActiveTripRequest() throws TripRequestException{
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("johndoe@mail.com");
////
////        LocationDTO startLocation = new LocationDTO();
////        startLocation.setDisplayName("Startplace");
////        startLocation.setLatitude(51.4);
////        startLocation.setLongitude(6.8);
////        LocationDTO endLocation = new LocationDTO();
////        endLocation.setDisplayName("Endplace");
////        endLocation.setLatitude(51.4);
////        endLocation.setLongitude(6.9);
////
////        TripRequestBody  body = new TripRequestBody();
////        body.setStartLocation(startLocation);
////        body.setEndLocation(endLocation);
////        body.setCarType(CarType.DELUXE);
////        //Expected: Throws TripRequestException, because active trip request already exists.
////        TripRequestException exception = assertThrows(TripRequestException.class, () -> tripRequestService.createCurrentActiveTripRequest(body, principal));
////        assertEquals("Trip request already exists.", exception.getMessage());
//    }
//
//    @Test
//    void testDeleteTripRequestSuccessfully() {
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("johndoe@mail.com");
////
////        tripRequestService.deleteCurrentActiveTripRequest(principal);
////
////        //Expected: TripRequest successfully "deleted" from repository.
////        assertFalse(tripRequestService.existsActiveTripRequest("johndoe@mail.com"));
//    }
//
//    @Test
//    void testDeleteTripRequestFailed() throws NotFoundException {
//        //Expected: TripRequest not found in repository -> NotFoundException
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("maxmustermann@mail.com");
////
////        //Expected: Throws NotFoundException, because user has no active trip request.
////        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripRequestService.deleteCurrentActiveTripRequest(principal));
////        assertEquals("Current customer does not have an active trip request.", exception.getMessage());
//    }
//
//    @Test
//    void testGetCurrentActiveTripRequestSuccessfully() {
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("johndoe@mail.com");
////        TripRequestDTO dto = tripRequestService.getCurrentActiveTripRequest(principal);
////
////        //Expected: Returns already existing active trip request of user johndoe@mail.com.
////        assertThat(dto.getStartLocation().getLatitude()).isEqualTo(51.44833475);
////        assertThat(dto.getStartLocation().getLongitude()).isEqualTo(6.903738267025127);
////        assertThat(dto.getEndLocation().getLatitude()).isEqualTo(51.42810685);
////        assertThat(dto.getEndLocation().getLongitude()).isEqualTo(6.93780940053893);
////        assertThat(dto.getCarType()).isEqualTo(CarType.MEDIUM);
////        assertThat(dto.getNote()).isEqualTo("Testnote");
//    }
//
//    @Test
//    void testGetCurrentActiveTripRequestFailed() throws NotFoundException {
////        principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("maxmustermann@mail.com");
////
////        //Expected: Throws NotFoundException, because user has no active trip request.
////        NotFoundException exception = assertThrows(NotFoundException.class, () -> tripRequestService.getCurrentActiveTripRequest(principal));
////        assertEquals("Current customer does not have an active trip request.", exception.getMessage());
//    }
}
