package com.sep.backend.trip.request;

import com.sep.backend.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents the body of a creation request for a trip.")
public class TripRequestBody {

    @Schema(description = "The start location of the trip.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Location startLocation;

    @Schema(description = "The end location of the trip.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Location endLocation;

    @Schema(description = "The type of car requested.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The optional notes by the customer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String note;

}
