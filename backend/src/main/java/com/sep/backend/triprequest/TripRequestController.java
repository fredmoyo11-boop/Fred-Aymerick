package com.sep.backend.triprequest;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
import com.sep.backend.triprequest.nominatim.NominatimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/map")
public class TripRequestController {
    private final TripRequestService tripRequestService;
    private final NominatimService nominatimService;

    public TripRequestController(TripRequestService tripRequestService, NominatimService nominatimService) {
        this.tripRequestService = tripRequestService;
        this.nominatimService = nominatimService;
    }

    @PostMapping("/search")
    @Operation(description = "Provides a suggested list of locations",
            tags = {Tags.TRIP_REQUEST},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Suggested list successful send",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripRequestDTO.class))))})
    public List<LocationDTO> suggestions(@Parameter(description = "Searched Location") @RequestParam String search) throws Exception {
        return nominatimService.getSuggestions(search);
    }

    @PostMapping("/request/create")
    @Operation(description = "Creates trip request and saves to repository",
            tags= {Tags.TRIP_REQUEST},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Trip request created successfully.",
                    content = @Content(schema = @Schema(implementation = TripRequestEntity.class)))})
     public void create(@RequestBody TripRequestDTO tripRequestDTO) {
        tripRequestService.createTripRequest(tripRequestDTO);

    }

    @GetMapping("/request/view")
    @Operation(description = "Shows trip request of customer",
            tags = {Tags.TRIP_REQUEST},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Trip request showed successfully",
                    content = @Content(schema = @Schema(implementation = TripRequestDTO.class)))})
    public TripRequestDTO view(@Parameter(description = "Uses principal to find request.") Principal principal) {
        return tripRequestService.showTripRequest(principal);
    }

    @DeleteMapping("/request/view/delete")
    @Operation(description = "Deletes trip request from repository",
            tags= {Tags.TRIP_REQUEST},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip request deleted successfully.",
                            content = @Content(schema = @Schema(implementation = TripRequestEntity.class)))})
    public void deleteRequest(@Parameter(description = "Uses principal to find request.") Principal principal) {
        tripRequestService.deleteTripRequest(principal);
    }
}
