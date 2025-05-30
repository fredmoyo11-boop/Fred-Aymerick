package com.sep.backend.nominatim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.ErrorMessages;
import com.sep.backend.entity.LocationEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.data.LocationDTO;
import com.sep.backend.nominatim.data.NominatimFeature;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.trip.request.TripRequestException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NominatimService {

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestClient restClient;
    private final RestClient orsClient;

    public NominatimService(@Value("${ors.api.key}") String apiKey) {
        this.apiKey = apiKey;

        this.orsClient = RestClient.builder()
                .baseUrl("https://api.openrouteservice.org/v2/directions/driving-car/geojson")
                .defaultHeader("Authorization", apiKey)
                .build();

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


    public Double getDistanceToTripRequests(LocationDTO driverLocation, LocationDTO tripStartLocation) throws DistanceNotFoundException {
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
            throw new DistanceNotFoundException("Fehler bei der Distanzberechnung: " + e.getMessage());
        }
    }

    public ORSFeatureCollection requestORSRoute(LocationEntity start, LocationEntity end, Optional<List<LocationEntity>> stops) throws JsonProcessingException {


        List<List<Double>> coordinates = new ArrayList<>();

        coordinates.add(List.of(start.getLongitude(), start.getLatitude()));

        if (stops.isPresent()) {

            for (LocationEntity stop : stops.get()) {

                coordinates.add(List.of(stop.getLongitude(), stop.getLatitude()));
            }
        }
        coordinates.add(List.of(end.getLongitude(), end.getLatitude()));

        String body = """
                {
                  "coordinates": %s
                }
                """.formatted(mapper.writeValueAsString(coordinates));

        String response = orsClient.post()
                .header("Authorization", apiKey)
                .body(body)
                .retrieve()
                .body(String.class);

        return mapper.readValue(response, ORSFeatureCollection.class);
    }

}
