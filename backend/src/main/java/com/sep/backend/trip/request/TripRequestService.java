package com.sep.backend.trip.request;

import com.sep.backend.CarTypes;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.*;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.DistanceNotFoundException;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.nominatim.data.LocationDTO;
import com.sep.backend.ors.data.ORSFeature;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.ors.data.ORSGeometry;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TripRequestService {
    private final NominatimService nominatimservice;
    private final TripHistorieRepository tripHistoryRepository;
    private final TripRequestRepository tripRequestRepository;
    private final LocationRepository locationRepository;

    private final AccountService accountService;

    public TripRequestService(NominatimService nominatimservice, TripHistorieRepository tripHistoryRepository, TripRequestRepository tripRequestRepository, LocationRepository locationRepository, AccountService accountService) {
        this.nominatimservice = nominatimservice;
        this.tripHistoryRepository = tripHistoryRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.locationRepository = locationRepository;
        this.accountService = accountService;
    }

    /**
     * Returns whether the user already has A active request or not.
     *
     * @param email The email address
     * @return Whether the customer has A active request or not
     */
    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_Email(email);
    }

    /**
     * Saves A LocationEntity to the Repository.
     *
     * @param location Location chosen by customer.
     * @return The location entity.
     */
    private LocationEntity saveLocation(@Valid Location location) {
//        var locationEntity = LocationEntity.from(locationDTO);
//        return locationRepository.save(locationEntity);
        return new LocationEntity();
    }

    /**
     * Returns the trip request entity by email and status.
     *
     * @param email         The email to find the trip request entity.
     * @param requestStatus The status of the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findTripRequestByEmailAndStatus(String email, String requestStatus) {
        return tripRequestRepository.findByCustomer_Email(email);
    }

    /**
     * Returns the trip request entity by email.
     *
     * @param email The email to find the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findActiveTripRequestByEmail(String email) {
        return findTripRequestByEmailAndStatus(email, TripRequestStatus.ACTIVE);
    }

    /**
     * Returns the trip request entity in form of a trip request DTO.
     *
     * @param principal The user.
     * @return The trip request DTO.
     * @throws NotFoundException If no active trip request entity found
     */
    public TripRequestDTO getCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        Optional<TripRequestEntity> tripRequestEntity = findActiveTripRequestByEmail(email);
        return tripRequestEntity.map(TripRequestDTO::from).orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
    }

    /**
     * Creates a trip request.
     *
     * @param tripRequestBody The main body containing trip request information.
     * @param principal       The user.
     * @return The trip request entity
     */
    @Transactional
    public TripRequestEntity createCurrentActiveTripRequest(@Valid TripRequestBody tripRequestBody, Principal principal) throws TripRequestException {
        String email = principal.getName();

        String role = accountService.getRoleByEmail(email);
        //checks if role of user is customer
        if (!Roles.CUSTOMER.equals(role)) {
            throw new TripRequestException("User must be a customer.");
        }
        //checks if car type in request is valid
        if (!CarTypes.isValidCarType(tripRequestBody.getDesiredCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }
        //only one active trip request at a time
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIP_REQUEST);
        }

        LocationEntity start = saveLocation(tripRequestBody.getStartLocation());
        LocationEntity end = saveLocation(tripRequestBody.getEndLocation());

        // 2. Koordinatenliste vorbereiten: [ [lon, lat], [lon, lat] ]
        List<List<Double>> coordinates = new ArrayList<>();
        coordinates.add(Arrays.asList(start.getLongitude(), start.getLatitude()));
        coordinates.add(Arrays.asList(end.getLongitude(), end.getLatitude()));

        // 3. ORSGeometry erstellen und Koordinaten setzen
        ORSGeometry geometry = new ORSGeometry();
        geometry.setCoordinates(coordinates);

        // 4. ORSFeature erstellen und Geometrie setzen
        ORSFeature feature = new ORSFeature();
        feature.setGeometry(geometry);

        // 5. ORSFeatureCollection erstellen und Feature setzen
        ORSFeatureCollection featureCollection = new ORSFeatureCollection();
        featureCollection.setFeatures(List.of(feature));

        // 6. RouteEntity erstellen und FeatureCollection setzen
        RouteEntity route = new RouteEntity();
        route.setStartLocation(start);
        route.setEndLocation(end);
        route.setStops(new ArrayList<>());
        route.setGeoJSON(featureCollection);

        // 7. TripRequestEntity aufbauen
        TripRequestEntity tripRequestEntity = new TripRequestEntity();
        tripRequestEntity.setRoute(route);                     // Route mit Geo-Daten
        tripRequestEntity.setDesiredCarType(tripRequestBody.getDesiredCarType());
        tripRequestEntity.setNote(tripRequestBody.getNote());
        tripRequestEntity.setStatus(TripRequestStatus.ACTIVE);

        // 8. Customer-Entity setzen
        CustomerEntity customerEntity = accountService.getCustomerByEmail(email);
        tripRequestEntity.setCustomer(customerEntity);

        // 9. Persistieren
        return tripRequestRepository.save(tripRequestEntity);
    }

    /**
     * Deletes the current active trip request.
     *
     * @param principal The user.
     * @throws NotFoundException If no active trip request found.
     */
    public void deleteCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        String email = principal.getName();
        TripRequestEntity tripRequestEntity = findActiveTripRequestByEmail(email)
                .orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
        tripRequestEntity.setStatus(TripRequestStatus.DELETED);

        tripRequestRepository.save(tripRequestEntity);
    }

    public List<TripHistoryDTO> geTripHistory(Principal principal) {
        String email = principal.getName();
        if (accountService.existsEmail(email)) {
            if(Roles.CUSTOMER.equals(accountService.getRoleByEmail(email))) {
                var customerEntity = accountService.getCustomerByEmail(email);
                return TripHistoryDTO.getTripHistoryDTO(tripHistoryRepository.findByCustomer(customerEntity));
            } else {
                var driverEntity = accountService.getDriverByEmail(email);
                return TripHistoryDTO.getTripHistoryDTO(tripHistoryRepository.findByDriver(driverEntity));
            }
        }else{
            throw new TripRequestException(ErrorMessages.HISTORY_NOT_FOUND);
        }
    }

    public List<AvailableTripRequestDTO> getAvailableRequests(LocationDTO driverLocation) {
        List<TripRequestEntity> activeRequests = tripRequestRepository.findByStatus(TripRequestStatus.ACTIVE);

        return activeRequests.stream().map(activeRequest ->
        {

          LocationEntity start = activeRequest.getRoute().getStartLocation();

                        LocationDTO tripStartLocation = new LocationDTO();
                        tripStartLocation.setLatitude(start.getLongitude());
                        tripStartLocation.setLongitude(start.getLatitude());
                        tripStartLocation.setDisplayName(start.getDisplayName());


                    Double distance= 0.0;

                    try {
                        distance = nominatimservice.getDistanceToTripRequests(driverLocation, tripStartLocation);

                    } catch (DistanceNotFoundException e) {
                        throw new RuntimeException(ErrorMessages.HISTORY_NOT_FOUND);
                    }


                    // Durchschnittliche Bewertung
            CustomerEntity customer = activeRequest.getCustomer();
            double avgRating = tripHistoryRepository.findByCustomer(customer).stream()
                            .mapToInt(TripHistoryEntity::getCustomerRating)
                            .average()
                            .orElse(0.0);

                    return new AvailableTripRequestDTO(
                            activeRequest.getId(),
                            activeRequest.getRequestTime(),
                            customer.getUsername(),
                            avgRating,
                            activeRequest.getDesiredCarType(),
                            distance
                    );
        }).toList();


//        Comparator<AvailableTripRequestDTO> comparator = getComparator(sort);
//
//        if ("desc".equalsIgnoreCase(direction)) {
//            comparator = comparator.reversed();
//        }
//        return unsorted.stream()
//                .sorted(comparator)
//                .collect(Collectors.toList());
    }

//    private Comparator<AvailableTripRequestDTO> getComparator(String sort) {
//
//        return switch (sort) {
//            case "requestTime" -> Comparator.comparing(AvailableTripRequestDTO::getRequestTime);
//            case "customerUsername" -> Comparator.comparing(AvailableTripRequestDTO::getCustomerUsername, String.CASE_INSENSITIVE_ORDER);
//            case "customerRating" -> Comparator.comparing(AvailableTripRequestDTO::getCustomerRating);
//            case "desiredCarType" ->  Comparator.comparingInt(dto ->
//                switch (dto.getDesiredCarType()) {
//                case "SMALL" -> 1;
//                case "MEDIUM" -> 2;
//                case "DELUXE" -> 3;
//                   default -> throw new IllegalStateException("Unexpected value: " + dto.getDesiredCarType());
//            });
//            default -> Comparator.comparing(AvailableTripRequestDTO::getDistanceInKm);
//        };
//    }
}
