package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NominatimService {

    private Location[] locations;

    public void getSuggestions(String location) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);

            locations = mapper.readValue(response, Location[].class);

        } catch (Exception e) {
            throw new Exception("Could not find location");
        }
    }

    public String getFinalAddress(String finalLocation) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + finalLocation;

            String response = restTemplate.getForObject(url, String.class);
            Location location = mapper.readValue(response, Location.class);
            return location.getLatitude() + "," + location.getLongitude();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
