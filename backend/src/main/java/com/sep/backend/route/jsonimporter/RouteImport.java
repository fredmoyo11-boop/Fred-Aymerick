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

    private RouteImport() {}

    public static String importRoute(String file, RouteRepository routeRepository, WaypointRepository waypointRepository) throws IOException {
        RouteEntity route = routeRepository.save(new RouteEntity());
        String Id = Long.toString(route.getId());
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
        WaypointEntity startEntity  = waypointRepository.findByRouteIdAndIndex(route.getId(),0).orElseThrow(() -> new NotFoundException(""));
        WaypointEntity endEntity    = waypointRepository.findByRouteIdAndIndex(route.getId(),i-1).orElseThrow(() -> new NotFoundException(""));
        startEntity.setType(WaypointType.START);
        endEntity.setType(WaypointType.END);
        waypointRepository.save(startEntity);
        waypointRepository.save(endEntity);

        return Id;
    }
}