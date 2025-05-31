package com.sep.backend.trip.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sep.backend.CarTypes;
import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.*;
import com.sep.backend.nominatim.DistanceNotFoundException;
import com.sep.backend.nominatim.LocationRepository;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.nominatim.data.LocationDTO;
import com.sep.backend.nominatim.data.NominatimFeature;
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
    private final NominatimService nominatimservice;
    private final TripHistorieRepository tripHistoryRepository;
    private final TripRequestRepository tripRequestRepository;
    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    private final AccountService accountService;

    public TripRequestService(NominatimService nominatimservice, TripHistorieRepository tripHistoryRepository, TripRequestRepository tripRequestRepository, LocationRepository locationRepository, RouteRepository routeRepository, AccountService accountService) {
        this.nominatimservice = nominatimservice;
        this.tripHistoryRepository = tripHistoryRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.locationRepository = locationRepository;
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
        return tripRequestRepository.existsByCustomer_Email(email);
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
    public TripRequestEntity createCurrentActiveTripRequest(@Valid TripRequestBody tripRequestBody, Principal principal) throws JsonProcessingException {

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

        LocationEntity start = createLocationWithGeoJson(tripRequestBody.getStartLocation());

        LocationEntity end = createLocationWithGeoJson(tripRequestBody.getEndLocation());

        List<LocationEntity> stops = getStopsEntities(tripRequestBody);

        ORSFeatureCollection geoJson = nominatimservice.requestORSRoute(start, end, Optional.ofNullable(stops));

        RouteEntity route = getRouteEntity(start, end, stops, geoJson);

        stops = stops == null ? null : stops.stream()
                .peek(stop -> stop.setRoute(route))
                .map(locationRepository::save)
                .toList();
        start.setRoute(route);
        end.setRoute(route);
        locationRepository.save(start);
        locationRepository.save(end);

        String carType = tripRequestBody.getDesiredCarType();

        Double calculatedPrice = getTotalPreis(geoJson, carType);


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

    public List<LocationEntity> getStopsEntities(TripRequestBody tripRequestBody) {
        return tripRequestBody.getStops().isEmpty() ? null : tripRequestBody.getStops()
                .stream()
                .map(this::createLocationWithGeoJson)
                .toList();
    }

    public RouteEntity getRouteEntity(LocationEntity start, LocationEntity end, List<LocationEntity> stops, ORSFeatureCollection geoJson) {
        RouteEntity route = new RouteEntity();
        route.setStartLocation(start);
        route.setEndLocation(end);
        route.setStops(stops);
        route.setGeoJSON(geoJson);
        return routeRepository.save(route);
    }

    public double getDistance(ORSFeatureCollection routeGeoJson) {
        return routeGeoJson.getFeatures().getFirst().getProperties().getSummary().getDistance() / 10000;
    }

    public Double getPricePerKm(String carType) {
        return switch (carType) {
            case CarTypes.SMALL -> 1.0;
            case CarTypes.MEDIUM -> 2.0;
            case CarTypes.DELUXE -> 10.0;
            default -> throw new TripRequestException(ErrorMessages.INVALID_CAR_TYPE);
        };
    }

    public double getTotalPreis(ORSFeatureCollection routeGeoJson, String carType) {
        return (routeGeoJson.getFeatures().getFirst().getProperties().getSummary().getDistance() / 10000) * getPricePerKm(carType);
    }


    public LocationEntity createLocationWithGeoJson(LocationDTO dto) {
        NominatimFeature geoJSON = nominatimservice.reverse(dto.getLatitude().toString(), dto.getLongitude().toString()).getFeatures().getFirst();
        LocationEntity loc = new LocationEntity();
        loc.setLatitude(dto.getLatitude());
        loc.setLongitude(dto.getLongitude());
        loc.setDisplayName(dto.getDisplayName());
        loc.setGeoJSON(geoJSON);
        return loc;
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

    public List<TripHistoryDTO> getTripHistory(Principal principal) {
        String email = principal.getName();
        if (accountService.existsEmail(email)) {
            if (Roles.CUSTOMER.equals(accountService.getRoleByEmail(email))) {
                var customerEntity = accountService.getCustomerByEmail(email);
                return TripHistoryDTO.getTripHistoryDTO(tripHistoryRepository.findByCustomer(customerEntity));
            } else {
                var driverEntity = accountService.getDriverByEmail(email);
                return TripHistoryDTO.getTripHistoryDTO(tripHistoryRepository.findByDriver(driverEntity));
            }
        } else {
            throw new TripRequestException(ErrorMessages.HISTORY_NOT_FOUND);
        }
    }

    public List<AvailableTripRequestDTO> getAvailableRequests(LocationDTO driverLocation) {
        List<TripRequestEntity> activeRequests = tripRequestRepository.findByStatus(TripRequestStatus.ACTIVE);

        return activeRequests.stream().map(activeRequest ->
        {

            LocationEntity start = activeRequest.getRoute().getStartLocation();

            LocationDTO tripStartLocation = new LocationDTO();
            tripStartLocation.setLatitude(start.getLatitude());
            tripStartLocation.setLongitude(start.getLongitude());
            tripStartLocation.setDisplayName(start.getDisplayName());


            Double distance = 0.0;

            try {
                distance = nominatimservice.getDistanceToTripRequests(driverLocation, tripStartLocation);

            } catch (DistanceNotFoundException e) {
                throw new RuntimeException(ErrorMessages.HISTORY_NOT_FOUND);
            }


            CustomerEntity customer = activeRequest.getCustomer();
            double avgRating = tripHistoryRepository.findByCustomer(customer).stream()
                    .mapToInt(TripHistoryEntity::getCustomerRating)
                    .average()
                    .orElse(0.0);

            Double tripDuration = activeRequest.getRoute().getGeoJSON().getFeatures().getFirst().getProperties().getSummary().getDuration();
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
