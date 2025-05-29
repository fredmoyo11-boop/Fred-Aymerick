package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Schema(description = "Represents a segment.")
public class ORSSegment {

    @JsonProperty("distance")
    @Schema(description = "The distance for the segment.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double distance;

    @JsonProperty("duration")
    @Schema(description = "The duration for the segment.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double duration;

    @JsonProperty("steps")
    @Schema(description = "The steps of the segment.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ORSStep> steps;
}
