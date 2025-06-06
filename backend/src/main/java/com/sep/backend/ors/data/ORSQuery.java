package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Represents a query.")
@JsonIgnoreProperties(ignoreUnknown = true)

public class ORSQuery {

    @JsonProperty("coordinates")
    @Schema(description = "The coordinates.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<List<Double>> coordinates;

    @JsonProperty("profile")
    @Schema(description = "The profile.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String profile;

    @JsonProperty("profileName")
    @Schema(description = "The name of the profile.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String profileName;

    @JsonProperty("format")
    @Schema(description = "The format.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String format;
}
