package com.sep.backend.route;

import com.sep.backend.entity.RouteEntity;
import com.sep.backend.location.Location;
import com.sep.backend.ors.data.ORSFeatureCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Represents the information of a route.")
public class Route {

    @Schema(description = "The id of the route.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long routeId;

    @Schema(description = "The stops of the route.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Location> stops;

    @Schema(description = "The ORS GeoJSON of the route.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSFeatureCollection geoJson;


    public static Route from(RouteEntity routeEntity) {
        var route = new Route();
        route.setRouteId(routeEntity.getId());
        route.setStops(routeEntity.getStops().stream().map(Location::from).toList());
        route.setGeoJson(routeEntity.getGeoJSON());
        return route;
    }
}
