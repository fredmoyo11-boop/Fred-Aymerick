package com.sep.backend.route;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {this.routeService = routeService;}

    @GetMapping("/{routeId}")
    @Operation(description = "Returns the route with the specified id.",
            tags = {Tags.ROUTE},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Route received successfully.",
                        content = @Content(schema = @Schema(implementation = RouteDTO.class))),
                @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Route does not exist.")
            })
    public RouteDTO getRoute(@PathVariable Long routeId) {
        return RouteDTO.from(routeService.getRoute(routeId));
    }
}
