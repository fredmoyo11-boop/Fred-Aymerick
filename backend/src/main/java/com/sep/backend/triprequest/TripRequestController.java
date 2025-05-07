package com.sep.backend.triprequest;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
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
@RequestMapping("/api/map")
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

    @GetMapping("/tripRequest/create")
     public void create(@RequestBody TripRequestDTO tripRequestDTO) {
        tripRequestService.upsertTripRequest(tripRequestDTO);
    }

    @GetMapping("/tripRequest/view/delete")
    public void deleteRequest(@RequestParam String username) {
        tripRequestService.deleteTripRequest(username);
    }
}
