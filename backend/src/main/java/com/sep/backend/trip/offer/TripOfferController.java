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

    @GetMapping("/customer/list")
    @Operation(description = "Returns the full offer list.",
            tags={Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offers returned.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripOfferResponse.class))))})
    public List<TripOfferResponse> getTripOfferList(Principal principal) {
        return tripOfferService.getTripOfferList(principal);
    }

}
