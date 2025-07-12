package com.sep.backend.trip.simulation;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.trip.simulation.data.SimulationAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/trip/simulation", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripSimulationController {

    private final TripSimulationService tripSimulationService;

    public TripSimulationController(TripSimulationService tripSimulationService) {
        this.tripSimulationService = tripSimulationService;
    }

    @PostMapping("/complete/{tripOfferId}")
    @Operation(description = "Completes a trip simulation for trip offer with specified id.",
            tags = {Tags.TRIP_SIMULATION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip simulation completed successfully."),})
    public void completeTrip(@PathVariable("tripOfferId") Long tripOfferId) {
        tripSimulationService.completeTrip(tripOfferId);
    }

    @PostMapping("/rate/{tripOfferId}")
    @Operation(description = "",
            tags = {Tags.TRIP_SIMULATION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip rated successfully.")})
    public void rateTrip(@PathVariable("tripOfferId") Long tripOfferId, @RequestBody Integer rating, Principal principal) {
        tripSimulationService.rateTrip(tripOfferId, rating, principal.getName());
    }

    @GetMapping("/action/{tripOfferId}")
    @Operation(description = "Returns all actions of a simulation.",
            tags = {Tags.TRIP_SIMULATION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Simulation actions retrieved successfully.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SimulationAction.class))))})
    public List<SimulationAction> getSimulationActions(@PathVariable("tripOfferId") Long tripOfferId) {
        return tripSimulationService.getSimulationActions(tripOfferId);
    }

    @GetMapping("/index/{tripOfferId}")
    @Operation(description = "Returns the last animation index.",
            tags = {Tags.TRIP_SIMULATION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Index retrieved successfully.",
                            content = @Content(schema = @Schema(implementation = Integer.class)))})
    public Integer getSimulationIndex(@PathVariable("tripOfferId") Long tripOfferId) {
        return tripSimulationService.getLastSimulationIndex(tripOfferId);
    }

}
