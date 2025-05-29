package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents the meta data produces by ORS.")
public class ORSMetadata {

    @JsonProperty("attribution")
    @Schema(description = "The attribution.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String attribution;

    @JsonProperty("service")
    @Schema(description = "The service.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String service;

    @JsonProperty("timestamp")
    @Schema(description = "The timestamp.", requiredMode = Schema.RequiredMode.REQUIRED)
    private long timestamp;

    @JsonProperty("query")
    @Schema(description = "The query.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSQuery query;

    @JsonProperty("engine")
    @Schema(description = "The engine.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSEngine engine;
}
