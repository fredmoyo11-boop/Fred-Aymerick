package com.sep.backend.trip.nominatim;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NominatimService {
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

            return mapper.readValue(response, new TypeReference<List<LocationDTO>>() {});
        } catch (Exception e) {
            throw new Exception("Could not find location", e);
        }
    }
}
