package com.sep.backend.trip.request;

import com.sep.backend.location.Location;
import com.sep.backend.ors.data.ORSFeatureCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Represents the body of a creation request for a trip.")
public class TripRequestBody {

    @NotNull
    @Size(min = 2)
    @Schema(description = "The list of locations made in the trip request.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Location> locations;

    @NotNull
    @Schema(description = "The ORS route for the trip", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSFeatureCollection geojson;

    @NotBlank
    @Schema(description = "The type of car requested.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The optional notes by the customer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String note;

}
