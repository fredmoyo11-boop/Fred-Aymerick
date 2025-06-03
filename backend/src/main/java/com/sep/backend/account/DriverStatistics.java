package com.sep.backend.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents additional statistics of a driver.")
public class DriverStatistics {

    @Schema(description = "The username of the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverUsername;

    @Schema(description = "The first name of the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverFirstName;

    @Schema(description = "The last name of the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverLastName;

    @Schema(description = "The average rating of the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double averageRating;

    @Schema(description = "The number of trips the driver has made.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalTrips;

    @Schema(description = "The total distance driven by the driver.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalDistance;
}
