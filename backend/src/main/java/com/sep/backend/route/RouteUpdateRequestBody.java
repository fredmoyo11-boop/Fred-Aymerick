package com.sep.backend.route;

import com.sep.backend.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RouteUpdateRequestBody {

    @Schema(description = "The current coordinate of the simulation.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Coordinate currentCoordinate;

    @Schema(description = "The locations of the route.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Location> locations;
}
