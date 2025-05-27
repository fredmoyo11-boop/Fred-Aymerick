package com.sep.backend.trip.nominatim;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import com.sep.backend.trip.request.DistanceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NominatimService {

    @Value("${ors.api.key}")
    private String apiKey;

    private final RestClient restClient1 = RestClient.builder()
            .baseUrl("https://api.openrouteservice.org")
            .build();

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
    public List<LocationDTO> searchLocations(String location) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("format", "json") //TODO GeoJSON benutzen
                            .queryParam("q", location)
                            .build())
                    .retrieve()
                    .body(String.class);

            System.out.println(response);

            return mapper.readValue(response, new TypeReference<List<LocationDTO>>() {
            });
        } catch (Exception e) {
            throw new Exception("Could not find location", e);
        }
    }

    public Double getDistanceToTripRequests(Double startLat, Double startLon, Double endLat, Double endLon) {
        try {
            String response = restClient1.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/directions/driving-car")
                            .queryParam("api_key", apiKey)
                            .queryParam("start", startLon + "," + startLat)
                            .queryParam("end", endLon + "," + endLat)
                            .build())
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            double distance = root
                    .path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("distance").asDouble();


            return distance / 1000.0;

        }catch (Exception e) {
            throw new DistanceNotFoundException(e.getMessage());
         }
    }


}





