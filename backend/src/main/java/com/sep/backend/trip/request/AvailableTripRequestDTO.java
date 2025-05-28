package com.sep.backend.trip.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
public class AvailableTripRequestDTO {

    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private Long requestId;
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime requestTime;
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerUsername;
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private double customerRating;
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private String carType;
    @Schema(  requiredMode = Schema.RequiredMode.REQUIRED)
    private double distanceInKm;
}

