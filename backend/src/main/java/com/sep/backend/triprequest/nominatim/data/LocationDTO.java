package com.sep.backend.triprequest.nominatim.data;

import com.sep.backend.triprequest.nominatim.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    @NotBlank
    @Schema(description = "Display name of location")
    private String displayName;

    @NotBlank
    @Schema(description = "Latitude of location")
    private Double latitude;

    @NotBlank
    @Schema(description = "Longitude of location")
    private Double longitude;


    public static LocationDTO from(LocationEntity locationEntity) {
        LocationDTO dto = new LocationDTO();
        dto.setLatitude(locationEntity.getLatitude());
        dto.setLongitude(locationEntity.getLongitude());
        dto.setDisplayName(locationEntity.getDisplayName());
        return dto;
    }
}
