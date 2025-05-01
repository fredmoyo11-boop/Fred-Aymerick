package com.sep.backend.requestDrive.nominatim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class NominatimService {

    public static void getCoordinates(String location) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;

        Location place = mapper.readValue(url, Location.class);

        System.out.println("Latitude: " + place.getLatitude());
        System.out.println("Longitude: " + place.getLongitude());

    }
}
