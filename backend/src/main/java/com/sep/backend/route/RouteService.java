package com.sep.backend.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sep.backend.NotFoundException;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.RouteEntity;
import com.sep.backend.location.Location;
import com.sep.backend.location.LocationService;
import com.sep.backend.nominatim.NominatimService;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.ORSService;
import com.sep.backend.ors.data.ORSFeatureCollection;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Service
public class RouteService {
    private final LocationService locationService;
    private final RouteRepository routeRepository;
    private final ORSService orsService;
    private final NominatimService nominatimService;

    public RouteService(LocationService locationService, RouteRepository routeRepository, ORSService orsService, NominatimService nominatimService) {
        this.locationService = locationService;
        this.routeRepository = routeRepository;
        this.orsService = orsService;
        this.nominatimService = nominatimService;
    }

    /**
     * Creates a routeEntity and saves it.
     *
     * @param geojson Given GeoJSON created by ORS.
     * @param locations List of Locations to be put in RouteEntity.
     * @return The RouteEntity.
     */
    @Transactional
    public RouteEntity createRoute(ORSFeatureCollection geojson, List<Location> locations) {
        var routeEntity = new RouteEntity();

        List<LocationEntity> stops = locationService.saveLocations(locations);
        log.debug("List of stops: {}", stops);

        stops.forEach(stop -> stop.setRoute(routeEntity));
        routeEntity.setStops(stops);

        log.debug("Locations in route: {}", routeEntity.getStops());
        routeEntity.setGeoJSON(geojson);
        return routeRepository.save(routeEntity);
    }

    /**
     * Gets the RouteEntity.
     *
     * @param routeId Given to find route in repository.
     * @return RouteEntity when found in repository.
     * @throws NotFoundException When Route not found.
     */
    public RouteEntity getRoute(Long routeId) throws NotFoundException {
        return routeRepository.findById(routeId).orElseThrow(() -> new NotFoundException("Route not found"));
    }

    public RouteEntity updateRoute(Long routeId, List<Location> updatedRouteStops, Coordinate currentCoordinate) throws NotFoundException, JsonProcessingException {
        var routeEntity = getRoute(routeId);

        //Gets all coordinates and creates new list with only visited coordinates
        List<Coordinate> currentRouteCoordinates = routeEntity.getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().stream().map(Coordinate::from).toList();
        List<Coordinate> alreadyVisitedCoordinates = getVisitedCoordinates(currentRouteCoordinates, currentCoordinate);

        //Out of the visited coordinates, gets all visited Locations
        List<Location> currentRouteStops = routeEntity.getStops().stream().map(Location::from).toList();
        List<Location> alreadyVisitedStops = getVisitedLocations(currentRouteStops, alreadyVisitedCoordinates);
        log.debug("All visited stops in route: {}", alreadyVisitedStops);

        //Out of all locations, counts how many of them are visited
        int maxPrefixLength = Math.min(updatedRouteStops.size(), alreadyVisitedStops.size());
        int prefixLength = IntStream.range(0, maxPrefixLength)
                .takeWhile(i -> {
                    var loc1 = alreadyVisitedStops.get(i);
                    var loc2 = updatedRouteStops.get(i);
                    return Objects.equals(loc1.getDisplayName(), loc2.getDisplayName()) && loc1.getCoordinate().getLatitude() == loc2.getCoordinate().getLatitude()
                            && loc1.getCoordinate().getLongitude() == loc2.getCoordinate().getLongitude();
                })
                .map(i -> 1)
                .sum();
        log.debug("Prefix length: {}", prefixLength);

        //Creates a sub-list of all new and unvisited stops added by user
        List<Location> newStops =  updatedRouteStops.subList(prefixLength, updatedRouteStops.size());
        log.debug("Sublist of unvisited stops: {}", newStops);
        //Removes unvisited stops to keep order
        routeEntity.getStops().subList(prefixLength, routeEntity.getStops().size()).clear();
        log.debug("routeEntity stop list after removing unvisited stops: {}:", routeEntity.getStops());
        //Creates a location on currentCoordinates and adds it to the list
        NominatimFeatureCollection currentLocationGeoJSON;
        try {
            currentLocationGeoJSON = nominatimService.reverse(String.valueOf(currentCoordinate.getLatitude()), String.valueOf(currentCoordinate.getLongitude()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Current coordinate is invalid.");
        }
        var currentLocation = Location.from(currentLocationGeoJSON.getFeatures().getFirst());
        routeEntity.getStops().add(prefixLength, locationService.saveLocation(currentLocation));
        log.debug("Updated list with current location: {}", routeEntity.getStops());
        //Saves new locations in repository and adds them to the list
        routeEntity.getStops().addAll(locationService.saveLocations(newStops));
        log.debug("Updated list with all stops: {}", routeEntity.getStops());
        routeEntity.getStops().forEach(stop -> stop.setRoute(routeEntity));

        //Gets coordinates of all stops and requests a new geoJSON by ORS
        List<Coordinate> newRoute = routeEntity.getStops().stream().map(Coordinate::from).toList();
        var geoJSON = orsService.getRouteDirections(newRoute);
        routeEntity.setGeoJSON(geoJSON);

        return routeRepository.save(routeEntity);
    }

    public Integer getLastVisitedLocationIndex(Long routeId, Coordinate currentCoordinate) throws NotFoundException {
        var routeEntity = getRoute(routeId);

        //Gets all coordinates and creates new list with only visited coordinates
        List<Coordinate> currentRouteCoordinates = routeEntity.getGeoJSON().getFeatures().getFirst().getGeometry().getCoordinates().stream().map(Coordinate::from).toList();
        List<Coordinate> alreadyVisitedCoordinates = getVisitedCoordinates(currentRouteCoordinates, currentCoordinate);
//        log.debug("List of visited coordinates: {}", alreadyVisitedCoordinates);

        //Out of the visited coordinates, gets all visited Locations
        List<Location> currentRouteStops = routeEntity.getStops().stream().map(Location::from).toList();
//        log.debug("List of all stops: {}", currentRouteStops);
        List<Location> alreadyVisitedStops = getVisitedLocations(currentRouteStops, alreadyVisitedCoordinates);
//        log.debug("List of visited stops: {}", alreadyVisitedStops);
//        log.debug("Size of alreadyVisitedStops: {}", alreadyVisitedStops.size());
        return alreadyVisitedStops.size();
    }



    public List<Coordinate> getVisitedCoordinates (List<Coordinate> coordinates, Coordinate currentCoordinate) {
        int lastVisitedCoordinateIndex = IntStream.range(0, coordinates.size())
                .boxed()
                .min(Comparator.comparingDouble(i -> coordinates.get(i).distanceTo(currentCoordinate)))
                .orElse(-1);
        if (lastVisitedCoordinateIndex == -1) {
            return coordinates;
        }
        return coordinates.subList(0, lastVisitedCoordinateIndex + 1);
    }

    public List<Location> getVisitedLocations (List<Location> routeStops, List<Coordinate> visitedCoordinates) {
        int lastVisitedLocationIndex = IntStream.range(0, routeStops.size())
                .filter(i -> !isCoordinateVisited(Coordinate.from(routeStops.get(i)), visitedCoordinates))
                .findFirst()
                .orElse(-1);
        if (lastVisitedLocationIndex == -1) {
            return routeStops;
        }
        return routeStops.subList(0, lastVisitedLocationIndex);

        //!visitedCoordinates.contains(Coordinate.from(routeStops.get(i)))
    }

    private boolean isCoordinateVisited(Coordinate stopCoordinate, List<Coordinate> visitedCoordinates) {
        double threshold = 0.005; //50 m threshold
        return visitedCoordinates.stream().anyMatch(visited -> stopCoordinate.distanceTo(visited) < threshold);
    }
}