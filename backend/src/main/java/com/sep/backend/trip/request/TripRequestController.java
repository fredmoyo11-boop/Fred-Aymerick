package com.sep.backend.trip.request;

import com.sep.backend.HttpStatus;
import com.sep.backend.NotFoundException;
import com.sep.backend.Tags;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import com.sep.backend.trip.nominatim.NominatimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trip/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripRequestController {
    private final TripRequestService tripRequestService;
    private final NominatimService nominatimService;

    public TripRequestController(TripRequestService tripRequestService, NominatimService nominatimService) {
        this.tripRequestService = tripRequestService;
        this.nominatimService = nominatimService;
    }

    @GetMapping("/search")
    @Operation(description = "Provides a suggested list of locations",
            tags = {Tags.TRIP_REQUEST},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Suggested list successful send",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocationDTO.class))))})
    public List<LocationDTO> searchLocations(@Parameter(description = "Searched Location") @RequestParam String query) throws Exception {
        return nominatimService.searchLocations(query);
    }

    @GetMapping("/current")
    @Operation(description = "Returns the trip request for the current customer.",
            tags = {Tags.TRIP_REQUEST},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip request retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = TripRequestDTO.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Active trip request does not exist.")})
    public TripRequestDTO getCurrentActiveTripRequest(Principal principal) {
        return tripRequestService.getCurrentActiveTripRequest(principal);
    }

    @PostMapping("/current")
    @Operation(description = "Creates a trip request for the current customer.",
            tags = {Tags.TRIP_REQUEST},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip request created successfully.",
                    content = @Content(schema = @Schema(implementation = TripRequestDTO.class)))})
    public TripRequestDTO createCurrentActiveTripRequest(@RequestBody @Valid TripRequestBody tripRequestBody, Principal principal) throws TripRequestException {
        return TripRequestDTO.from(tripRequestService.createCurrentActiveTripRequest(tripRequestBody, principal));
    }

    @DeleteMapping("/current")
    @Operation(description = "Deletes the active trip request for the current customer.",
            tags = {Tags.TRIP_REQUEST},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Active trip request deleted successfully."),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Active trip request does not exist for current customer."),
            })
    public void deleteCurrentActiveTripRequest(Principal principal) throws NotFoundException {
        tripRequestService.deleteCurrentActiveTripRequest(principal);
    }

}
