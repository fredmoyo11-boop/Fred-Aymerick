package com.sep.backend.entity;

import com.sep.backend.trip.nominatim.data.LocationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
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
@Entity
public class LocationEntity extends AbstractEntity {

    @NotBlank
    @Schema(description = "The display name of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "display_name")
    String display_name;

    @NotNull
    @Schema(description = "The latitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "latitude")
    Double lat;

    @NotNull
    @Schema(description = "The longitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "longitude")
    Double lon;

    public static LocationEntity from(@Valid LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDisplay_name(locationDTO.getDisplayName());
        locationEntity.setLat(locationDTO.getLatitude());
        locationEntity.setLon(locationDTO.getLongitude());
        return locationEntity;
    }
}
