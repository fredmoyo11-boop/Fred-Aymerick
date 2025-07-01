package com.sep.backend.statistics;


import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import com.sep.backend.trip.offer.TripOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/year")
    @Operation(description = "Gets statistics for a year.",
            tags = {Tags.STATISTICS},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Statistics retrieved successfully.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Number.class))))})
    public List<Number> getStatisticsForYear(String type, int year, Principal principal) {
        return statisticsService.getStatisticsForYear(type, year, principal);
    }

    @GetMapping("/month")
    @Operation(description = "Gets statistics for a month.",
            tags = {Tags.STATISTICS},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Statistics retrieved successfully.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Number.class))))})
    public List<Number> getStatisticsForMonth(String type, int year, int month, Principal principal) {
        return statisticsService.getStatisticsForMonth(type, year, month, principal);
    }
}
