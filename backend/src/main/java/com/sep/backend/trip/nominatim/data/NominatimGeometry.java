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
@Schema(description = "Represents a GeoJSON Geometry.")
public class NominatimGeometry {

    @JsonProperty("type")
    @Schema(description = "The geometry type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("coordinates")
    @Schema(description = "The coordinates of the geometry.")
    private List<Double> coordinates;
}
