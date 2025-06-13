package com.sep.backend.nominatim.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

public class NominatimFeature {

    @NotNull
    @JsonProperty("type")
    @Schema(description = "The feature type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @NotNull
    @JsonProperty("bbox")
    @Schema(description = "The bounding box.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Double> bbox;

    @NotNull
    @JsonProperty("properties")
    @Schema(description = "The properties.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NominatimProperties properties;

    @NotNull
    @JsonProperty("geometry")
    @Schema(description = "The geometry.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NominatimGeometry geometry;

}
