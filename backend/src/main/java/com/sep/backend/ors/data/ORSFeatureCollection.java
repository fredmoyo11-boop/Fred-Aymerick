package com.sep.backend.ors.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents the GeoJSON FeatureCollection.")
public class ORSFeatureCollection {

    @JsonProperty("type")
    @Schema(description = "The feature collection type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("bbox")
    @Schema(description = "The bounding box.", requiredMode = Schema.RequiredMode.REQUIRED)
    public List<Double> bbox;

    @JsonProperty("features")
    @Schema(description = "The features.", requiredMode = Schema.RequiredMode.REQUIRED)
    public List<ORSFeature> features = new ArrayList<>();

    @JsonProperty("metadata")
    @Schema(description = "The meta data produces by ORS.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSMetadata metadata;
}
