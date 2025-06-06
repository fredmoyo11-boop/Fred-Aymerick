package com.sep.backend.trip.offer;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/trip/offer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripOfferController {

    private final TripOfferService tripOfferService;

    public TripOfferController(TripOfferService tripOfferService) {
        this.tripOfferService = tripOfferService;
    }

    @GetMapping("/{tripOfferId}")
    @Operation(description = "Returns the trip offer with the specified id.",
            tags = {Tags.TRIP_OFFER},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer retrieved successfully.",
                            content = {@Content(schema = @Schema(implementation = TripOffer.class))}),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Trip offer does not exists.")})
    public TripOffer getTripOffer(@PathVariable Long tripOfferId) {
        return TripOffer.from(tripOfferService.getTripOfferEntity(tripOfferId));
    }

}
