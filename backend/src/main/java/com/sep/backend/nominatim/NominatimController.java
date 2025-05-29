package com.sep.backend.nominatim;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.location.Location;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/nominatim", produces = MediaType.APPLICATION_JSON_VALUE)
public class NominatimController {

    private final NominatimService nominatimService;

    public NominatimController(NominatimService nominatimService) {
        this.nominatimService = nominatimService;
    }

    @GetMapping("/search")
    @Operation(description = "Returns a NominatimFeatureCollection containing locations based on the given query.",
            tags = {Tags.NOMINATIM},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Locations retrieved successfully.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Location.class))))})
    public List<Location> search(String query) {
        return nominatimService.search(query);
    }
}
