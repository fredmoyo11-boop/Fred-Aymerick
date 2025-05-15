package com.sep.backend.route.jsonimporter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.sep.backend.entity.RouteEntity;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.route.RouteRepository;
import com.sep.backend.route.WaypointRepository;
import com.sep.backend.route.WaypointType;
import com.sep.backend.NotFoundException;
import java.io.IOException;
import java.lang.Math;

public final class RouteImport {

    static final long MAX_NODES = 10000L;

    private RouteImport() {}

    public static String importRoute(String file, RouteRepository routeRepository, WaypointRepository waypointRepository) throws IOException {
        RouteEntity route = routeRepository.save(new RouteEntity());
        String id = Long.toString(route.getId());
        JsonNode node = new ObjectMapper().readTree(file);

        long i = 0L;
        while(true) {
            String longitude = node.at("/features/0/geometry/coordinates/" + Long.toString(i) + "/0").asText();
            String latitude = node.at("/features/0/geometry/coordinates/" + Long.toString(i) + "/1").asText();
            if(longitude.equals("") || latitude.equals("")) {
               break;
            }
            WaypointEntity we = new WaypointEntity();
            we.setIndex(i);
            we.setRouteId(route.getId());
            we.setType(WaypointType.POINT);
            we.setLongitude(longitude);
            we.setLatitude(latitude);
            waypointRepository.save(we);

            i++;
        }
        WaypointEntity startEntity  = waypointRepository.findByRouteIdAndIndex(route.getId(),0L).orElseThrow(() -> new NotFoundException(""));
        WaypointEntity endEntity    = waypointRepository.findByRouteIdAndIndex(route.getId(),i-1).orElseThrow(() -> new NotFoundException(""));
        startEntity.setType(WaypointType.START);
        endEntity.setType(WaypointType.END);
        waypointRepository.save(startEntity);
        waypointRepository.save(endEntity);

        long k = 1L;
        while(true) {
            String midLongitudeString = node.at("/features/" + Long.toString(k) + "/geometry/coordinates/0").asText();
            String midLatitudeString = node.at("/features/" + Long.toString(k) + "/geometry/coordinates/1").asText();
            if(midLongitudeString.equals("") || midLatitudeString.equals("")) {
                break;
            }
            Double midLongitude = Double.valueOf(midLongitudeString);
            Double midLatitude = Double.valueOf(midLatitudeString);
            long j = 1L;
            long currentIndex = 0L;
            double currentDistance = Double.MAX_VALUE;
            while (j < i - 1) {
                WaypointEntity we = waypointRepository.findByRouteIdAndIndex(route.getId(),j).orElseThrow(() -> new NotFoundException(""));
                Double currentLongitude = Double.valueOf(we.getLongitude());
                Double currentLatitude = Double.valueOf(we.getLatitude());
                double distance = Math.sqrt((Math.pow((currentLongitude - midLongitude),2)) + (Math.pow((currentLatitude - midLatitude),2)));
                if(distance < currentDistance) {
                    currentDistance = distance;
                    currentIndex = j;
                }
                j++;
            }
            WaypointEntity we = waypointRepository.findByRouteIdAndIndex(route.getId(),currentIndex).orElseThrow(() -> new NotFoundException(""));
            we.setType(WaypointType.MID);
            waypointRepository.save(we);
            k++;
        }
        return id;
    }
}