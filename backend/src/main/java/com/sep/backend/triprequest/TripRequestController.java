package com.sep.backend.triprequest;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.LocationDTO;
import com.sep.backend.triprequest.nominatim.NominatimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

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
            tags = {Tags.TRIP},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Suggested list successful send",
                    content = @Content(schema = @Schema(implementation = LocationDTO.class)))})
    public List<LocationDTO> suggestions(@Parameter(description = "Searched Location") @RequestParam String search) throws Exception {
        return nominatimService.getSuggestions(search);
    }

    @PostMapping("/request/create")
    @Operation(description = "Creates trip request and saves to repository",
            tags= {Tags.TRIP},
            responses = {
                @ApiResponse(responseCode = HttpStatus.OK, description = "Trip request created successfully.",
                    content = @Content(schema = @Schema(implementation = TripRequestEntity.class)))})
     public void create(@RequestBody TripRequestDTO tripRequestDTO) {
        tripRequestService.createTripRequest(tripRequestDTO);

    }

    @PostMapping("/request/view/delete")
    @Operation(description = "Deletes trip request from repository",
            tags= {Tags.TRIP},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip request deleted successfully.",
                            content = @Content(schema = @Schema(implementation = TripRequestEntity.class)))})
    public void deleteRequest(@RequestParam String username) {
        tripRequestService.deleteTripRequest(username);
    }
}
