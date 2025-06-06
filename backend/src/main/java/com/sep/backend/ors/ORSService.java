package com.sep.backend.ors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.route.Coordinate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ORSService {

    @Value("${ors.api_key}")
    private String orsApiKey; //TODO Move orsApiKey from application.properties to .env

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeaders(headers -> {
                    headers.add("Authorization", orsApiKey);
                })
                .build();
    }

    public ORSFeatureCollection getRouteDirections(List<Coordinate> coordinates) throws ORSException {
        System.out.println(orsApiKey);
        //ORS expects coordinates in the form of [longitude, latitude]
        ObjectMapper mapper = new ObjectMapper();

        //Get coordinates [lon, lat] from coordinates and put them in List
        List<List<Double>> stopCoordinates = coordinates.stream()
                .map(feature -> List.of(feature.getLongitude(), feature.getLatitude()))
                .toList();
        //Temporarily saves the list in a body
        var routeRequestBody = new RouteRequestBody(stopCoordinates);
        //Makes post request to ORS and gets a string back in form of geojson
        String orsFeatureCollectionString = restClient.post()
                .uri("/v2/directions/driving-car/geojson")
                .contentType(MediaType.APPLICATION_JSON)
                .body(routeRequestBody)
                .retrieve()
                .body(String.class);
        System.out.println(orsFeatureCollectionString);
        try {
            //Tries to convert the string into the object ORSFeatureCollection
            return mapper.readValue(orsFeatureCollectionString, ORSFeatureCollection.class);
        } catch (JsonProcessingException e) {
            throw new ORSException("Could not parse ors feature collection");
        }
    }
}
