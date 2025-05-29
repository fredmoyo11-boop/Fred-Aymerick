package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(description = "Represents the summary of a request made by ORS.")
public class ORSSummary {

    @JsonProperty("distance")
    @Schema(description = "The total distance.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double distance;

    @JsonProperty("duration")
    @Schema(description = "The total duration.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double duration;
}
