package com.sep.backend.trip.simulation.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents all possible parameters of different simulation actions.")
public class SimulationActionParameters {
    @Schema(description = "The start index of the simulation.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer startIndex;

    @Schema(description = "The velocity of the simulation.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer velocity;

    @Schema(description = "The rating of the user.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer rating;

}
