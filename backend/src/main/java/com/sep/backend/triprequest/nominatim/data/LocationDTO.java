package com.sep.backend.triprequest.nominatim.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.triprequest.nominatim.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Location")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDTO {

    @Schema(description = "Display name of location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("display_name")
    @NotBlank
    private String displayName;

    @Schema(description = "Latitude of location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lat")
    @NotNull
    private Double latitude;

    @Schema(description = "Longitude of location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lon")
    @NotNull
    private Double longitude;


    public static LocationDTO from(LocationEntity locationEntity) {
        LocationDTO dto = new LocationDTO();
        dto.setLatitude(locationEntity.getLatitude());
        dto.setLongitude(locationEntity.getLongitude());
        dto.setDisplayName(locationEntity.getDisplayName());
        return dto;
    }
}
