package com.sep.backend.trip.nominatim.data;

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
@Schema(description = "Represents a GeoJSON feature.")
public class NominatimFeature {

    @JsonProperty("type")
    @Schema(description = "The feature type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("bbox")
    @Schema(description = "The bounding box.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Double> bbox;

    @JsonProperty("properties")
    @Schema(description = "The properties.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NominatimProperties properties;

    @JsonProperty("geometry")
    @Schema(description = "The geometry.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NominatimGeometry geometry;
}
