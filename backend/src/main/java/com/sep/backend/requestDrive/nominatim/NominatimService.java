package com.sep.backend.requestDrive.nominatim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class NominatimService {

    public void getCoordinates(String location) throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);

            Location[] places = mapper.readValue(response, Location[].class);

            if (places.length > 0) {
                Location place = places[0];
                System.out.println("Latitude: " + place.getLatitude()+ "\n" + "Longitude: " + place.getLongitude());
                System.out.println(place.getPlaceID());
                System.out.println(place.getDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
