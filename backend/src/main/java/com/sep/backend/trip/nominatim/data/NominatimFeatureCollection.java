package com.sep.backend.trip.nominatim.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a GeoJSON FeatureCollection.")
public class NominatimFeatureCollection {

    @JsonProperty("type")
    @Schema(description = "The feature collection type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("licence")
    @Schema(description = "The licence.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String licence;

    @JsonProperty("bbox")
    @Schema(description = "The bounding box.", requiredMode = Schema.RequiredMode.REQUIRED)
    public List<Double> bbox;

    @JsonProperty("features")
    @Schema(description = "The features.", requiredMode = Schema.RequiredMode.REQUIRED)
    public List<NominatimFeature> features;
}
