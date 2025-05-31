package com.sep.backend.nominatim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.ErrorMessages;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.data.LocationDTO;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.trip.request.ORSRequestException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NominatimService {

    private final String apiKey;
    private final ObjectMapper mapper;
    private final RestClient restClient;
    private final RestClient orsClient;
    private static final Logger log = LoggerFactory.getLogger(NominatimService.class);

    public NominatimService(@Value("${ors.api.key}") String apiKey, ObjectMapper mapper) {

        this.apiKey = apiKey;

        this.mapper = mapper;

        this.orsClient = RestClient.builder()
                .baseUrl("https://api.openrouteservice.org/v2/directions/driving-car/geojson")
                .defaultHeader("Authorization", apiKey)
                .build();

        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("Content-Type", "application/json")
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


    public Double getDistanceToTripRequests(@Valid LocationDTO driverLocation, @Valid LocationDTO tripStartLocation) {
        try {

            String response = orsClient.post()
                    .header("Authorization", apiKey)
                    .body("""
                            {
                              "coordinates": [
                                [%f, %f],
                                [%f, %f]
                              ]
                            }
                            """.formatted(driverLocation.getLongitude(), driverLocation.getLatitude(), tripStartLocation.getLongitude(), tripStartLocation.getLatitude()))
                    .retrieve()
                    .body(String.class);

            ORSFeatureCollection result = this.mapper.readValue(response, ORSFeatureCollection.class);

            return result
                    .getFeatures()
                    .getFirst()
                    .getProperties()
                    .getSegments()
                    .getFirst()
                    .getDistance() / 1000.0;


        } catch (Exception e) {
            throw new ORSRequestException(ErrorMessages.ORS_PROCESSING_FAILED + e.getMessage());
        }
    }

    public ORSFeatureCollection requestORSRoute(LocationEntity start, LocationEntity end, Optional<List<LocationEntity>> stops) {

        try {
            List<List<Double>> coordinates = new ArrayList<>();

            //startpunkt - coordinate nehmen
            coordinates.add(List.of(start.getLongitude(), start.getLatitude()));

            //zwischenstopps-coordinate nehmen wenn sie existieren
            stops.ifPresent(stopList -> coordinates.addAll(stopList.stream()
                    .map(locationPair -> List.of(locationPair.getLongitude(), locationPair.getLatitude()))
                    .toList()));

            //endpunkt - coordinate nehmen
            coordinates.add(List.of(end.getLongitude(), end.getLatitude()));
            log.info("Gesendete Koordinaten an ORS: {}", coordinates);


            //Body der post-request
            String body = """
                    {
                      "coordinates": %s
                    }
                    """.formatted(mapper.writeValueAsString(coordinates));

            log.info("Starte ORS-Routenberechnung von '{}' nach '{}'", start.getDisplayName(), end.getDisplayName());
            String response = orsClient.post()
                    .header("Authorization", apiKey)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            log.info("ORS-Route erfolgreich empfangen für {} Wegpunkte", coordinates.size());
            return mapper.readValue(response, ORSFeatureCollection.class);

        } catch (JsonProcessingException e) {
            throw new ORSRequestException(ErrorMessages.ORS_PROCESSING_FAILED + e.getMessage());
        }
    }

}
