package com.sep.backend.route;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.route.RouteResponse;
import com.sep.backend.route.RouteRequest;
import com.sep.backend.route.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/route", produces = MediaType.APPLICATION_JSON_VALUE)
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/health")
    @Operation(description = "Returns the status of the route controller.",
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route controller healthy.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse health() {
        return new StringResponse("OK");
    }

    @GetMapping("/route_metadata")
    @Operation(description = "Returns start, end, count of midpoints and count of path waypoints from the route.",
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Route metadata returned.",
                            content = @Content(schema = @Schema(implementation = RouteResponse.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schema = @Schema(implementation = RouteRequest.class)
                    )
            )
    )
    public RouteResponse getMetadata(@Parameter(description = "The route id.") @RequestPart("id") RouteDTO id) {
        return routeService.getRouteById(id);
    }


}
