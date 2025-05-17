package com.sep.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class LocationEntity extends AbstractEntity {

    @NotBlank
    @Schema(description = "The display name of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "display_name")
    String displayName;

    @NotNull
    @Schema(description = "The latitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "lat")
    Double latitude;

    @NotNull
    @Schema(description = "The longitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "lon")
    Double longitude;

    public static LocationEntity from(@Valid LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDisplayName(locationDTO.getDisplayName());
        locationEntity.setLatitude(locationDTO.getLatitude());
        locationEntity.setLongitude(locationDTO.getLongitude());
        return locationEntity;
    }
}
