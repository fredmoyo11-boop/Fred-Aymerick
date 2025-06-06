package com.sep.backend.trip.offer;

import com.sep.backend.StringResponse;

import com.sep.backend.HttpStatus;
import com.sep.backend.NotFoundException;
import com.sep.backend.Tags;
import com.sep.backend.trip.offer.response.*;
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
@RequestMapping(value = "/api/trip/offer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripOfferController {
    private final TripOfferService tripOfferService;

    public TripOfferController(TripOfferService tripOfferService) {
        this.tripOfferService = tripOfferService;
    }

    @GetMapping("/driver/exists")
    @Operation(description = "Checks whether a driver has an active offer or not.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer status returned.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse hasActiveOffer(Principal principal) {
        return new StringResponse(tripOfferService.hasActiveTripOffer(principal));
    }

    @PostMapping("/driver/withdraw")
    @Operation(description = "Withdraw an offer.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer withdrawn.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Driver does not have an offer.")})
    public StringResponse withdrawOffer(Principal principal) {
        return new StringResponse(tripOfferService.withdrawOffer(principal));
    }

    @PostMapping("/customer/accept")
    @Operation(description = "Accept an offer.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer accepted.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Driver does not have an offer.")})
    public StringResponse acceptOffer(String driverUsername, Principal principal) {
        return new StringResponse(tripOfferService.acceptOffer(driverUsername, principal));
    }

    @PostMapping("/customer/decline")
    @Operation(description = "Decline an offer.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer declined.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Driver does not have an offer.")})
    public StringResponse declineOffer(String driverUsername, Principal principal) {
        return new StringResponse(tripOfferService.declineOffer(driverUsername, principal));
    }

    @GetMapping("/customer/list")
    @Operation(description = "Returns the full offer list.",
            tags={Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offers returned.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripOfferResponse.class))))})
    public List<TripOfferResponse> getTripOfferList(Principal principal) {
        return tripOfferService.getTripOfferList(principal);
	}

    @GetMapping()
    @Operation(description = "scan",
            responses = {
                    @ApiResponse(description = "scan", responseCode = HttpStatus.OK,
                            content = {@Content(schema = @Schema(implementation = TripOffer.class))})})
    public TripOffer getTripOffer() {
        return new TripOffer();
    }

}
