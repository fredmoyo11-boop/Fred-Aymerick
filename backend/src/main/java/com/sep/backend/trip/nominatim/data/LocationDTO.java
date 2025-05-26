package com.sep.backend.trip.nominatim.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.entity.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(description = "Represents a DTO for a location.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationDTO {

    @Schema(description = "The display name of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("display_name")
    private String displayName;

    @Schema(description = "The latitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lat")
    private Double latitude;

    @Schema(description = "The longitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lon")
    private Double longitude;


    public static LocationDTO from(LocationEntity locationEntity) {
        LocationDTO dto = new LocationDTO();
        dto.setLatitude(locationEntity.getLat());
        dto.setLongitude(locationEntity.getLon());
        dto.setDisplayName(locationEntity.getDisplay_name());
        return dto;
    }
}
