package com.sep.backend.route;

import com.sep.backend.route.RouteRepository;
import com.sep.backend.route.*;
import org.springframework.stereotype.Service;

@Service
public class RouteService {
    long id;

    public RouteResponse getRouteById(RouteDTO id) {
        this.id = id.getRouteid();
        String startLatitude;
        String startLongitude;
        String endLatitude;
        String endLongitude;
        long otherPointCount;
        long midpointCount;
        return new RouteResponse();
    }


}