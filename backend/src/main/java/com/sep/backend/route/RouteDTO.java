package com.sep.backend.route;

import com.sep.backend.location.Location;
import com.sep.backend.ors.data.ORSFeatureCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

import java.util.List;

@Data
@Schema(description = "Represents a Route request.")
public class RouteDTO {

    @Schema(description = "The route id.", implementation = RouteDTO.class, requiredMode = RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "A list of stops.")
    private List<Location> stops;

    @Schema(description = "GeoJSON provided by ORS for the route.")
    private ORSFeatureCollection geojson;
}
