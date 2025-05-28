package com.sep.backend.nominatim;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.data.NominatimFeatureCollection;
import com.sep.backend.ors.data.ORSFeatureCollection;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NominatimService {

    @Value("${ors.api.key}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .build();


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



    public Double getDistanceToTripRequests(Double startLat, Double startLon, Double endLat, Double endLon) throws DistanceNotFoundException {
        try {
            RestClient orsClient = RestClient.builder()
                    .baseUrl("https://api.openrouteservice.org")
                    .defaultHeader("Authorization", apiKey)
                    .build();


            String response = orsClient.post()
                    .uri("/v2/directions/driving-car/geojson")
                    .header("Authorization", apiKey)
                    .body("""
                {
                  "coordinates": [
                    [%f, %f],
                    [%f, %f]
                  ]
                }
                """.formatted(startLon, startLat, endLon, endLat))
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


}
