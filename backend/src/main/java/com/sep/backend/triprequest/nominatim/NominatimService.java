package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NominatimService {

    public void getSuggestions(String location) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);

            Location[] places = mapper.readValue(response, Location[].class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not find location");
        }
    }


}
