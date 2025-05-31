package com.sep.backend.TripHistory;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/trip", produces = MediaType.APPLICATION_JSON_VALUE)

public class TripHistoryController {

    public final TripHistoryService tripHistoryService;

    public TripHistoryController(TripHistoryService tripHistoryService) {
        this.tripHistoryService = tripHistoryService;
    }


    @Operation(description = "Fahranfrage-History eines Fahrers oder eines Kunden ",
            tags = {Tags.TRIP_REQUEST},
            responses = {@ApiResponse(responseCode = HttpStatus.OK,
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TripHistoryDTO.class))))})
    @GetMapping("/history")
    public ResponseEntity<List<TripHistoryDTO>> getTripHistory(Principal principal) {
        return ResponseEntity.ok(tripHistoryService.getTripHistoryDTOs(principal));
    }

}
