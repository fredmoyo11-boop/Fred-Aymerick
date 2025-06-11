package com.sep.backend.route;

import com.sep.backend.entity.RouteEntity;
import com.sep.backend.location.Location;
import com.sep.backend.ors.data.ORSFeatureCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "Represents a Route request.")
public class RouteDTO {

    @Schema(description = "The route id.", implementation = RouteDTO.class, requiredMode = RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "A list of stops.")
    private List<Location> stops;

    @Schema(description = "GeoJSON provided by ORS for the route.")
    private ORSFeatureCollection geojson;

    public static RouteDTO from(RouteEntity routeEntity) {
        var dto = new RouteDTO();
        dto.setId(routeEntity.getId());
        dto.setStops(routeEntity.getStops().stream().map(Location::from).toList());
        dto.setGeojson(routeEntity.getGeoJSON());
        return dto;
    }
}
