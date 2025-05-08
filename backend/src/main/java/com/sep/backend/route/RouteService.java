package com.sep.backend.route;

import com.sep.backend.NotFoundException;
import com.sep.backend.route.*;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.entity.RouteEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RouteService {
    long id;
    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;

    public RouteService(RouteRepository routeRepository, WaypointRepository waypointRepository) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
    }

    public RouteResponse getRouteById(RouteDTO id) {
        this.id = id.getRouteId();

        WaypointEntity startEntity = waypointRepository
                                            .findByIdAndType(this.id, WaypointType.START)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));
        WaypointEntity endEntity = waypointRepository
                                            .findByIdAndType(this.id, WaypointType.END)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));

        String startLongitude = startEntity.getLongitude();
        String startLatitude = startEntity.getLatitude();
        String endLongitude = endEntity.getLongitude();
        String endLatitude = endEntity.getLatitude();
        long otherPointCount = waypointRepository.countByIdAndType(this.id, WaypointType.POINT);
        long midpointCount = waypointRepository.countByIdAndType(this.id, WaypointType.MID);;
        return new RouteResponse(startLongitude, startLatitude, endLongitude, endLatitude, otherPointCount, midpointCount);
    }


}