package com.sep.backend.nominatim.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Schema(description = "Represents a DTO for a location.")
public class LocationDTO {

    @Schema(description = "The latitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Double latitude;

    @Schema(description = "The longitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Double longitude;

}
