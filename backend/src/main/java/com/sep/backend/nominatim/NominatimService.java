package com.sep.backend.nominatim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.ErrorMessages;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.trip.request.ORSRequestException;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class NominatimService {

    private static final Logger log = LoggerFactory.getLogger(NominatimService.class);
    private final ObjectMapper mapper;
    private final RestClient restClient;

    public NominatimService( ObjectMapper mapper) {


        this.mapper = mapper;


        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .build();
    }

    /**
     * Returns a NominatimFeatureCollection containing locations based on the given query.
     *
     * @param query The query.
     * @return The NominatimFeatureCollection containing the locations as features.
     */
    public List<Location> search(String query) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("format", "geojson")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .body(String.class);
        try {
            var nominatimFeatureCollection = mapper.readValue(response, NominatimFeatureCollection.class);
            return nominatimFeatureCollection.getFeatures().stream().map(Location::from).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reverses a location based on the provided coordinate.
     *
     * @param latitude  The latitude.
     * @param longitude The longitude.
     * @return The NominatimFeatureCollection containing the location as a feature.
     */
    public NominatimFeatureCollection reverse(@NotBlank String latitude, @NotBlank String longitude) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "geojson")
                        .queryParam("addressdetails", 0)
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .retrieve()
                .body(String.class);

        try {
            return mapper.readValue(response, NominatimFeatureCollection.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}
