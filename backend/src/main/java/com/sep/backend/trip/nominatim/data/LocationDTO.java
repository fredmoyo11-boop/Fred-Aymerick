package com.sep.backend.trip.nominatim.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.entity.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private String display_name;

    @Schema(description = "Latitude of location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lat")
    private Double lat;

    @Schema(description = "Longitude of location", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lon")
    private Double lon;


    public static LocationDTO from(LocationEntity locationEntity) {
        LocationDTO dto = new LocationDTO();
        dto.setLon(locationEntity.getLat());
        dto.setLon(locationEntity.getLon());
        dto.setDisplay_name(locationEntity.getDisplay_name());
        return dto;
    }
}
