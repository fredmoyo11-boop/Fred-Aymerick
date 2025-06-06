package com.sep.backend.trip.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Represents the relevant information of trip request for a driver.")
public class TripRequestInformationDTO {
    @Schema(description = "The id of the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tripRequestId;

    @Schema(description = "The time the trip was requested.", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime requestTime;

    @Schema(description = "The distance of the driver to the start point of the trip.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double driverDistance;

    @Schema(description = "The username of the customer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerUsername;

    @Schema(description = "The rating of the customer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double customerRating;

    @Schema(description = "The request car type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The distance of the trip in m.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distance;

    @Schema(description = "The duration of the trip in s.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration;

    @Schema(description = "The price of the trip in euro.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;
}
