package com.sep.backend.route.response;

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
public class WaypointResponse {
    @Schema(description = "Index of Point", requiredMode = RequiredMode.REQUIRED)
    private Long index;

    @Schema(description = "Type of Point", requiredMode = RequiredMode.REQUIRED)
    private String type;

    @Schema(description = "Longitude", requiredMode = RequiredMode.REQUIRED)
    private String longitude;

    @Schema(description = "Latitude", requiredMode = RequiredMode.REQUIRED)
    private String latitude;
}
