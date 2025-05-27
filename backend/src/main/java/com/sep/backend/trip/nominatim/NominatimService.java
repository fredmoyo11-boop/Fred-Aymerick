package com.sep.backend.trip.nominatim;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.trip.nominatim.data.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NominatimService {

    private final LocationRepository locationRepository;

    public NominatimService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    //Sets the restClient base URL
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .build();

    /**
     * Takes the input by the user and forwards it too nominatim.openstreetmap.org.
     *
     * @param location Input of user
     * @return A list of suggestions based on the query by nominatim
     * @throws Exception If location not found
     */
    public NominatimFeatureCollection searchLocations(String location) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("format", "geojson") //TODO GeoJSON benutzen
                            .queryParam("q", location)
                            .build())
                    .retrieve()
                    .body(String.class);
            return mapper.readValue(response, NominatimFeatureCollection.class);
        } catch (Exception e) {
            throw new Exception("Could not find location", e);
        }
    }

    public LocationEntity saveLocation(@Valid NominatimFeature feature) {
        NominatimProperties properties = feature.getProperties();
        NominatimGeometry geometry = feature.getGeometry();

        List<Double> coordinate = geometry.getCoordinates();

        var locationEntity = new LocationEntity();
        locationEntity.setDisplay_name(properties.getDisplayName());
        locationEntity.setLat(coordinate.getFirst());
        locationEntity.setLon(coordinate.getLast());

        return locationRepository.save(locationEntity);
    }
}
