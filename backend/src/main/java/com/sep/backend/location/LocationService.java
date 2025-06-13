package com.sep.backend.location;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.nominatim.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }


    public  LocationEntity saveLocation(Location location) {
        var locationEntity = new LocationEntity();
        locationEntity.setDisplayName(location.getDisplayName());
        locationEntity.setLongitude(location.getCoordinate().getLongitude());
        locationEntity.setLatitude(location.getCoordinate().getLatitude());
        locationEntity.setGeoJSON(location.getGeoJSON());
        return locationRepository.save(locationEntity);
    }

    public void deleteLocation(Long locationId) {
        locationRepository.deleteById(locationId);
    }

    @Transactional
    public List<LocationEntity> saveLocations(List<Location> locations) {
        return locations.stream().map(this::saveLocation).toList();
    }
}
