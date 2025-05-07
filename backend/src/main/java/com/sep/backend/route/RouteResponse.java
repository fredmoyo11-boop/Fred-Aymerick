package com.sep.backend.route;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents Route Metadata.")
public class RouteResponse {

    @Schema(description = "Longitude from Startpoint.", requiredMode = RequiredMode.REQUIRED)
    private String startLongitude;

    @Schema(description = "Latitude from Startpoint.", requiredMode = RequiredMode.REQUIRED)
    private String startLatitude;

    @Schema(description = "Longitude from Endpoint.", requiredMode = RequiredMode.REQUIRED)
    private String endLongitude;

    @Schema(description = "Latitude from Endpoint.", requiredMode = RequiredMode.REQUIRED)
    private String endLatitude;

    @Schema(description = "Amount of other points.", requiredMode = RequiredMode.REQUIRED)
    private long otherPointCount;

    @Schema(description = "Amount of midpoints.", requiredMode = RequiredMode.REQUIRED)
    private long midpointCount;

}
