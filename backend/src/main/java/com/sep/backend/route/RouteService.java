package com.sep.backend.route;

import com.sep.backend.NotFoundException;
import com.sep.backend.route.*;
import com.sep.backend.route.response.*;
import com.sep.backend.route.jsonimporter.*;
import com.sep.backend.entity.WaypointEntity;
import com.sep.backend.entity.RouteEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final WaypointRepository waypointRepository;

    public RouteService(RouteRepository routeRepository, WaypointRepository waypointRepository) {
        this.routeRepository = routeRepository;
        this.waypointRepository = waypointRepository;
    }

    public RouteResponse getRouteById(Long id) {
        WaypointEntity startEntity = waypointRepository
                                            .findByRouteIdAndType(id, WaypointType.START)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));
        WaypointEntity endEntity = waypointRepository
                                            .findByRouteIdAndType(id, WaypointType.END)
                                            .orElseThrow(() -> new NotFoundException(RouteErrorMessages.INVALID_ROUTE_ID));

        String startLongitude = startEntity.getLongitude();
        String startLatitude = startEntity.getLatitude();
        String endLongitude = endEntity.getLongitude();
        String endLatitude = endEntity.getLatitude();
        long otherPointCount = waypointRepository.countByRouteIdAndType(id, WaypointType.POINT);
        long midpointCount = waypointRepository.countByRouteIdAndType(id, WaypointType.MID);
        return new RouteResponse(startLongitude, startLatitude, endLongitude, endLatitude, otherPointCount, midpointCount);
    }

    public List<WaypointResponse> getMidpointsById(Long id) {
        List<WaypointResponse> midpointList = new ArrayList<WaypointResponse>();
        midpointList.addAll(mapWaypointEntityToWaypointResponse(waypointRepository.findAllPointsByRouteIdAndType(id,WaypointType.MID)));
        return midpointList;
    }

    public List<WaypointResponse> getFullRouteById(Long id) {
        List<WaypointResponse> waypointList = new ArrayList<WaypointResponse>();
        waypointList.addAll(mapWaypointEntityToWaypointResponse(waypointRepository.findAllPointsByRouteId(id)));
        return waypointList;
    }

    public String importGeoJson(MultipartFile file) {
        if(!(file.getContentType().equals("application/json"))) {
            return "Unsupported Content Type";
        }
        try {
            return RouteImport.importRoute(new String(file.getBytes()), routeRepository, waypointRepository);
        }
        catch (IOException e) {
            return "failed to import route";
        }
    }

    private List<WaypointResponse> mapWaypointEntityToWaypointResponse(List<WaypointEntity> entityList) {
        List<WaypointResponse> waypointResponseList = new ArrayList<WaypointResponse>();
        for(WaypointEntity waypointEntity : entityList) {
            waypointResponseList.add(new WaypointResponse(waypointEntity.getIndex(),waypointEntity.getType(),waypointEntity.getLongitude(),waypointEntity.getLatitude()));
        }
        return waypointResponseList;
    }
}