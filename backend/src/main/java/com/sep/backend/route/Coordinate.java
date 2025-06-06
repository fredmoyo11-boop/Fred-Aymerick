package com.sep.backend.route;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Represents a coordinate.")
public class Coordinate {

    @Schema(description = "The latitude.")
    private double latitude;

    @Schema(description = "The longitude.")
    private double longitude;
}
