package com.sep.backend.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {this.routeService = routeService;}

    @GetMapping("/{routeId}")
    @Operation(description = "Returns the route with the specified id.",
            tags = {Tags.ROUTE, Tags.ORS},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Route received successfully.",
                        content = @Content(schema = @Schema(implementation = Route.class))),
                @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Route does not exist.")
            })
    public Route getRoute(@PathVariable Long routeId) {
        return Route.from(routeService.getRoute(routeId));
    }

    @PutMapping("/{routeId}")
    @Operation(description = "Returns the route with the specified id.",
            tags = {Tags.ROUTE, Tags.ORS},
            responses = {
            @ApiResponse(responseCode = HttpStatus.OK, description = "Route updated successfully.",
                content = @Content(schema = @Schema(implementation = Route.class))),
            })
    public Route updateRoute(@PathVariable Long routeId, @RequestBody RouteUpdateRequestBody routeUpdateRequestBody) throws JsonProcessingException {
        return Route.from(routeService.updateRoute(routeId, routeUpdateRequestBody.getLocations(), routeUpdateRequestBody.getCurrentCoordinate()));
    }

    @PutMapping("/{routeId}/visited")
    @Operation(description = "Returns the index of the last visited Location.",
            tags = {Tags.ROUTE},
            responses = {
            @ApiResponse(responseCode = HttpStatus.OK, description = "Last index found.",
                content = @Content(schema = @Schema(implementation = Integer.class))),
            })
    public Integer lastVisitedIndex(@PathVariable Long routeId, @RequestBody RouteUpdateRequestBody routeUpdateRequestBody) {
        return routeService.getLastVisitedLocationIndex(routeId, routeUpdateRequestBody.getCurrentCoordinate());
    }
}