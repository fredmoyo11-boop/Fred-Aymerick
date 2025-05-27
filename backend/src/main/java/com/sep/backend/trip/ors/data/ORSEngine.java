package com.sep.backend.trip.ors.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the ORS engine information.")
public class ORSEngine {

    @JsonProperty("version")
    @Schema(description = "The version of the engine.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

    @JsonProperty("build_date")
    @Schema(description = "The build date of the engine.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buildDate;

    @JsonProperty("graph_date")
    @Schema(description = "The graph date of the engine.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String graphDate;
}
