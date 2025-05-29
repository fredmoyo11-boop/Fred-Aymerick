package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Represents a step.")
public class ORSStep {

    @JsonProperty("distance")
    @Schema(description = "The distance.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double distance;

    @JsonProperty("duration")
    @Schema(description = "The duration.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double duration;

    @JsonProperty("type")
    @Schema(description = "The type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int type;

    @JsonProperty("instruction")
    @Schema(description = "The instruction.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String instruction;

    @JsonProperty("name")
    @Schema(description = "The name.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @JsonProperty("way_points")
    @Schema(description = "The way points.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int[] wayPoints;
}
