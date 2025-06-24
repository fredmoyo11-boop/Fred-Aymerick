package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a GeoJSON Feature as standardized by RFC 7946.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ORSFeature {

    @JsonProperty("type")
    @Schema(description = "The feature type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("bbox")
    @Schema(description = "The bounding box.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Double> bbox;

    @JsonProperty("properties")
    @Schema(description = "The properties.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSProperties properties;

    @JsonProperty("geometry")
    @Schema(description = "The geometry.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSGeometry geometry;
}
