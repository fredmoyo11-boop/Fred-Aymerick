package com.sep.backend.nominatim.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a GeoJSON Geometry as standardized by RFC 7946.")
public class NominatimGeometry {
    @JsonProperty("type")
    @Schema(description = "The geometry type.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("coordinates")
    @Size(min = 2, max = 2)
    @Schema(description = "The coordinates of the geometry.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Double> coordinates;
}
