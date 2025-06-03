package com.sep.backend.trip.offer;

import com.sep.backend.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/trip/offer", produces = MediaType.APPLICATION_JSON_VALUE)
public class TripOfferController {

    @GetMapping()
    @Operation(description = "scan",
            responses = {
                    @ApiResponse(description = "scan", responseCode = HttpStatus.OK,
                            content = {@Content(schema = @Schema(implementation = TripOffer.class))})})
    public TripOffer getTripOffer() {
        return new TripOffer();
    }

}
