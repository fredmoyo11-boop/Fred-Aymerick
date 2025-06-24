package com.sep.backend.trip.offer;

import com.sep.backend.StringResponse;

import com.sep.backend.HttpStatus;

import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

    @PostMapping("/driver/new")
    @Operation(description = "Create a new offer.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer created.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Unable to find trip request"),
                    @ApiResponse(responseCode = HttpStatus.FORBIDDEN, description = "Driver has an active trip offer.")})
    public StringResponse createNewTripOffer(Long tripRequestId, Principal principal) {
        return new StringResponse(tripOfferService.createNewTripOffer(tripRequestId, principal));
    }
    
    @PostMapping("/accepted/{tripRequestId}")
    @Operation(description = "Returns the accepted trip offer belonging to the specified trip request id.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = TripOffer.class)))})
    public TripOffer getAcceptedTripOffer(@PathVariable Long tripRequestId) {
        return tripOfferService.getAcceptedTripOffer(tripRequestId);
    }


    @PostMapping("/{id}/revoke")
    @Operation(description = "Revokes the trip offer with the specified id.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer revoked."),
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer does not exists.")})
    public void revokeTripOffer(@PathVariable Long id, Principal principal) {
        tripOfferService.revokeTripOffer(id, principal);
    }

    @PostMapping("/{id}/accept")
    @Operation(description = "Accepts the trip offer with the specified id.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer accepted.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Trip offer does not exist.")})
    public void acceptTripOffer(@PathVariable Long id, Principal principal) {
        tripOfferService.acceptTripOffer(id, principal);
    }

    @PostMapping("/{id}/reject")
    @Operation(description = "Rejects the trip offer with the specified id.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offer rejected.",
                    content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Trip offer does not exist.")})
    public void rejectTripOffer(@PathVariable Long id, Principal principal) {
        tripOfferService.rejectTripOffer(id, principal);
    }

    @GetMapping("/current")
    @Operation(description = "Returns the trip offers for the current customer.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Trip offers retrieved successfully.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TripOffer.class))))})
    public List<TripOffer> getCurrentTripOffers(Principal principal) {
        return tripOfferService.getTripOfferList(principal);
    }

    @GetMapping("/current/active")
    @Operation(description = "Returns the active trip offer for the current driver.",
            tags = {Tags.TRIP_OFFER},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Active trip offer retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = TripOffer.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "No active trip offer found.")})
    public TripOffer getCurrentActiveTripOffer(Principal principal) {
        return tripOfferService.getCurrentActiveTripOffer(principal);
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
