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

public final class RouteImport {

    static final long MAX_NODES = 10000L;

    private RouteImport() {}

    public static String importRoute(String file, RouteRepository routeRepository, WaypointRepository waypointRepository) throws IOException {
        RouteEntity route = routeRepository.save(new RouteEntity());
        String id = Long.toString(route.getId());
        JsonNode node = new ObjectMapper().readTree(file);

        long i = 0L;
        while(true) {
            String longitude = node.path("features/0/geometry/coordinates/" + Long.toString(i) + "/0").asText();
            String latitude = node.path("features/0/geometry/coordinates/" + Long.toString(i) + "/1").asText();
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
            String midLongitude = node.path("features/" + Long.toString(k) + "/geometry/coordinates/0").asText();
            String midLatitude = node.path("features/" + Long.toString(k) + "/geometry/coordinates/1").asText();
            if(k>=i-1) {
                break;
            }
            long j = 1L;
            while (j < i - 1) {
                
            }
        }
        return id;
    }
}