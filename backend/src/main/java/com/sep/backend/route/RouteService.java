package com.sep.backend.route;

import com.sep.backend.NotFoundException;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.RouteEntity;
import com.sep.backend.location.Location;
import com.sep.backend.location.LocationService;
import com.sep.backend.ors.data.ORSFeatureCollection;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RouteService {
    private final LocationService locationService;
    private final RouteRepository routeRepository;

    public RouteService(LocationService locationService, RouteRepository routeRepository) {
        this.locationService = locationService;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public RouteEntity createRoute(ORSFeatureCollection featureCollection, List<Location> locations) {
        var routeEntity = new RouteEntity();

        List<LocationEntity> stops = locationService.saveLocations(locations);
        stops.forEach(stop -> stop.setRoute(routeEntity));
        routeEntity.setStops(stops);
        routeEntity.setGeoJSON(featureCollection);
        return routeRepository.save(routeEntity);
    }

    public RouteEntity getRoute(Long routeId) throws NotFoundException {
        return routeRepository.findById(routeId).orElseThrow(() -> new NotFoundException("Route not found"));
    }
}