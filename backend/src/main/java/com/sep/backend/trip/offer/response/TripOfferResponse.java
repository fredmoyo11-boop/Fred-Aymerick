package com.sep.backend.trip.offer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a trip offer.")
public class TripOfferResponse {

    @Schema(description = "Username", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "First name", requiredMode = RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Last name", requiredMode = RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Rating", requiredMode = RequiredMode.NOT_REQUIRED)
    private Double rating;

    @Schema(description = "Total drive count", requiredMode = RequiredMode.REQUIRED)
    private Long totalDriveCount;

    @Schema(description = "Total drive distance", requiredMode = RequiredMode.REQUIRED)
    private Double driveDistance;

}
