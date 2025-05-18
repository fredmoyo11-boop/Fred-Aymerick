package com.sep.backend.route;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.route.response.RouteResponse;
import com.sep.backend.route.response.WaypointResponse;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping(value = "/api/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/health")
    @Operation(description = "Returns the status of the route controller.",
            tags={Tags.ROUTE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route controller healthy.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse health() {
        return new StringResponse("OK");
    }

    @GetMapping("/metadata/{id}")
    @Operation(description = "Returns start, end, count of midpoints and count of path waypoints from the route.",
            tags={Tags.ROUTE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route metadata returned.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WaypointResponse.class))))}
    )
    public RouteResponse getMetadata(@Parameter(description = "The route id.") @PathVariable("id") Long id) {
        return routeService.getRouteById(id);
    }

    @GetMapping("/midpoints/{id}")
    @Operation(description = "Returns all midpoints from the route.",
            tags={Tags.ROUTE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route midpoints returned.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WaypointResponse.class))))}
    )
    public List<WaypointResponse> getMidpoints(@Parameter(description = "The route id") @PathVariable("id") Long id) {
        return routeService.getMidpointsById(id);
    }

    @GetMapping("/full/{id}")
    @Operation(description = "Returns the full route.",
            tags={Tags.ROUTE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route returned.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = WaypointResponse.class))))}
    )
    public List<WaypointResponse> getFullRoute(@Parameter(description = "The route id") @PathVariable("id") Long id) {
        return routeService.getFullRouteById(id);
    }

    @PostMapping("/import/geojson")
    @Operation(description = "Accepts and Imports route from geoJSON.",
            tags={Tags.ROUTE},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "geoJSON accepted.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = FileRequest.class)
                    )
            ))
    public StringResponse importRoute(@Parameter(description = "geoJSON file") @RequestPart(value = "file") MultipartFile file) {
        return new StringResponse(routeService.importGeoJson(file));
    }
}
