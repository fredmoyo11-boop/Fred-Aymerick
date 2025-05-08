package com.sep.backend.route;

import com.sep.backend.NotFoundException;
import com.sep.backend.route.*;
import com.sep.backend.route.response.*;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.entity.RouteEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;

    public RouteService(RouteRepository routeRepository, WaypointRepository waypointRepository) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
    }

    public RouteResponse getRouteById(RouteDTO Id) {
        WaypointEntity startEntity = waypointRepository
                                            .findByIdAndType(Id.getRouteId(), WaypointType.START)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));
        WaypointEntity endEntity = waypointRepository
                                            .findByIdAndType(Id.getRouteId(), WaypointType.END)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));

        String startLongitude = startEntity.getLongitude();
        String startLatitude = startEntity.getLatitude();
        String endLongitude = endEntity.getLongitude();
        String endLatitude = endEntity.getLatitude();
        long otherPointCount = waypointRepository.countByIdAndType(Id.getRouteId(), WaypointType.POINT);
        long midpointCount = waypointRepository.countByIdAndType(Id.getRouteId(), WaypointType.MID);
        return new RouteResponse(startLongitude, startLatitude, endLongitude, endLatitude, otherPointCount, midpointCount);
    }

    public List<WaypointResponse> getMidpointsById(RouteDTO Id) {
        List<WaypointResponse> midpointList = new ArrayList<WaypointResponse>();
        midpointList.addAll(mapWaypointEntityToWaypointResponse(waypointRepository.findAllPointsByIdAndType(Id.getRouteId(),WaypointType.MID)));
        return midpointList;
    }

    public List<WaypointResponse> getFullRouteById(RouteDTO Id) {
        List<WaypointResponse> waypointList = new ArrayList<WaypointResponse>();
        waypointList.addAll(mapWaypointEntityToWaypointResponse(waypointRepository.findAllPointsById(Id.getRouteId())));
        return waypointList;
    }

    public String importGeoJson(MultipartFile file) {
        return "Unavailable";
    }

    private List<WaypointResponse> mapWaypointEntityToWaypointResponse(List<WaypointEntity> entityList) {
        List<WaypointResponse> waypointResponseList = new ArrayList<WaypointResponse>();
        for(WaypointEntity waypointEntity : entityList) {
            waypointResponseList.add(new WaypointResponse(waypointEntity.getIndex(),waypointEntity.getType(),waypointEntity.getLongitude(),waypointEntity.getLatitude()));
        }
        return waypointResponseList;
    }
}