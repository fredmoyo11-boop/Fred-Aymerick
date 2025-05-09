package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NominatimService {


    public List<LocationDTO> getSuggestions(String location) throws Exception {
        try { //TODO: RestClient benutzen
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);

            return mapper.readValue(response, new TypeReference<List<LocationDTO>>() {
            });

        } catch (Exception e) {
            throw new Exception("Could not find location");
        }
    }

}
