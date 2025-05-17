package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class NominatimService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .build();

    public List<LocationDTO> getSuggestions(String location) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("format", "json") //TODO GeoJSON benutzen -> Entity/DTO ändern
                            .queryParam("q", location)
                            .build())
                    .retrieve()
                    .body(String.class);
            return mapper.readValue(response, new TypeReference<List<LocationDTO>>() {});
        } catch (Exception e) {
            throw new Exception("Could not find location", e);
        }
    }
}
