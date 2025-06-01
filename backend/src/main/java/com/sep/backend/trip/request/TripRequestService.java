package com.sep.backend.trip.request;

import com.sep.backend.CarTypes;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.*;
import com.sep.backend.location.Location;
import com.sep.backend.location.LocationService;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.route.RouteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TripRequestService {
    private final NominatimService nominatimService;
    private final TripHistorieRepository tripHistoryRepository;
    private final TripRequestRepository tripRequestRepository;
    private final LocationService locationService;
    private final RouteRepository routeRepository;
    private final AccountService accountService;

    public TripRequestService(NominatimService nominatimService, TripHistorieRepository tripHistoryRepository, TripRequestRepository tripRequestRepository, LocationService locationService, RouteRepository routeRepository, AccountService accountService) {
        this.nominatimService = nominatimService;
        this.tripHistoryRepository = tripHistoryRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.locationService = locationService;
        this.routeRepository = routeRepository;
        this.accountService = accountService;
    }

    /**
     * Returns whether the user already has A active request or not.
     *
     * @param email The email address
     * @return Whether the customer has A active request or not
     */
    public boolean existsActiveTripRequest(String email) {
        return tripRequestRepository.existsByCustomer_EmailAndStatus(email,TripRequestStatus.ACTIVE);
    }

    /**
     * Saves A LocationEntity to the Repository.
     *
     * @param location Location chosen by customer.
     * @return The location entity.
     */

    /**
     * Returns the trip request entity by email and status.
     *
     * @param email         The email to find the trip request entity.
     * @param requestStatus The status of the trip request entity.
     * @return The optional containing the trip request entity.
     */
    public Optional<TripRequestEntity> findTripRequestByEmailAndStatus(String email, String requestStatus) {
        return tripRequestRepository.findByCustomer_EmailAndStatus(email,requestStatus);
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
    public TripRequestEntity createCurrentActiveTripRequest( @Valid TripRequestBody tripRequestBody, Principal principal) {

        String email = principal.getName();

        String role = accountService.getRoleByEmail(email);
        if (!Roles.CUSTOMER.equals(role)) {
            throw new TripRequestException("User must be a customer.");
        }
        if (!CarTypes.isValidCarType(tripRequestBody.getDesiredCarType())) {
            throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        }
        if (existsActiveTripRequest(email)) {
            throw new TripRequestException(ErrorMessages.ALREADY_EXISTS_TRIP_REQUEST);
        }


        List<LocationEntity> stops = List.of(tripRequestBody.getStartLocation(),tripRequestBody.getEndLocation()).stream()
                .map(stop->Location.from(nominatimService.reverse(stop.getLatitude().toString(),stop.getLongitude().toString()).getFeatures().getFirst()))
                .map(locationService::saveLocation)
                .toList();
        ORSFeatureCollection geoJson = nominatimService.requestORSRoute(stops);

        RouteEntity route = saveRoute(stops, geoJson);

        String carType = tripRequestBody.getDesiredCarType();

        Double calculatedPrice = getTotalPrice(geoJson, carType);

        TripRequestEntity trip = new TripRequestEntity();
        trip.setCustomer(accountService.getCustomerByEmail(email));
        trip.setRoute(route);
        trip.setDesiredCarType(tripRequestBody.getDesiredCarType());
        trip.setNote(tripRequestBody.getNote());
        trip.setRequestTime(LocalDateTime.now());
        trip.setStatus(TripRequestStatus.ACTIVE);
        trip.setPrice(calculatedPrice);
        return tripRequestRepository.save(trip);
    }



    public RouteEntity saveRoute(List<LocationEntity> stops,  ORSFeatureCollection geoJson) {
        RouteEntity route = new RouteEntity();
        route.setStops(stops);
        route.setGeoJSON(geoJson);
        return routeRepository.save(route);
    }

    public double getDistance(ORSFeatureCollection routeGeoJson) {
        return routeGeoJson.getFeatures().getFirst().getProperties().getSummary().getDistance() / 10000;
    }

    public double getTotalPrice(ORSFeatureCollection routeGeoJson, String carType) {
        return (routeGeoJson.getFeatures().getFirst().getProperties().getSummary().getDistance() / 10000) * CarTypes.getPricePerKilometer(carType);
    }


    /**
     * Deletes the current active trip request.
     *
     * @param principal The user.
     * @throws NotFoundException If no active trip request found.
     */
    public void deleteCurrentActiveTripRequest(Principal principal) {
        String email = principal.getName();
        TripRequestEntity tripRequestEntity = findActiveTripRequestByEmail(email)
                .orElseThrow(() -> new NotFoundException("Current customer does not have an active trip request."));
        tripRequestEntity.setStatus(TripRequestStatus.DELETED);
        tripRequestRepository.save(tripRequestEntity);
    }



    public List<AvailableTripRequestDTO> getAvailableRequests(@Valid Location driverLocation) {
        List<TripRequestEntity> activeRequests = tripRequestRepository.findByStatus(TripRequestStatus.ACTIVE);

        return activeRequests.stream().map(activeRequest ->
        {

            LocationEntity start = activeRequest.getRoute().getStops().getFirst();

            Location tripStartLocation = new Location();
            tripStartLocation.setLatitude(start.getLatitude());
            tripStartLocation.setLongitude(start.getLongitude());
            tripStartLocation.setDisplayName(start.getDisplayName());


            Double distance = 0.0;

            distance = nominatimService.requestDistanceToTripRequests(driverLocation, tripStartLocation);


            CustomerEntity customer = activeRequest.getCustomer();
            double avgRating = tripHistoryRepository.findByCustomer(customer).stream()
                    .mapToInt(TripHistoryEntity::getCustomerRating)
                    .average()
                    .orElse(0.0);

            double tripDuration = activeRequest.getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDuration();
            return new AvailableTripRequestDTO(
                    activeRequest.getId(),
                    activeRequest.getRequestTime(),
                    customer.getUsername(),
                    avgRating,
                    activeRequest.getDesiredCarType(),
                    distance,
                    getDistance(activeRequest.getRoute().getGeoJSON()),
                    activeRequest.getPrice(),
                    tripDuration
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
