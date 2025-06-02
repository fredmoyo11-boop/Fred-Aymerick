package com.sep.backend.ors;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.ors.data.ORSFeatureCollection;
import com.sep.backend.route.Coordinate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ors", produces = MediaType.APPLICATION_JSON_VALUE)
public class ORSController {

    private final ORSService orsService;

    public ORSController(ORSService orsService) {this.orsService = orsService;}

    @PostMapping("directions/geojson")
    @Operation(description = "Returns a route based on a given list of coordinates.",
            tags = {Tags.ORS},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Route received successfully.",
                    content = @Content(schema = @Schema(implementation = ORSFeatureCollection.class)))})
    public ORSFeatureCollection getRoute(@RequestBody List<Coordinate> coordinates) throws Exception {
        return orsService.getRouteDirections(coordinates);
    }
}
