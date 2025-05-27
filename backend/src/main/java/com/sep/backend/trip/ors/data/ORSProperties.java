package com.sep.backend.trip.ors.data;

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
@Schema(description = "Represents the properties of a feature.")
public class ORSProperties {

    @JsonProperty("segments")
    @Schema(description = "The segments.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ORSSegment> segments;

    @JsonProperty("way_points")
    @Schema(description = "The way points.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Integer> wayPoints;

    @JsonProperty("summary")
    @Schema(description = "The summary.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ORSSummary summary;
}
