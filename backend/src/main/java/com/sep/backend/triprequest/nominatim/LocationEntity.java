package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.entity.AbstractEntity;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Entity for Location")
public class LocationEntity extends AbstractEntity {

    @NotBlank
    @Column(name = "display_name")
    @Schema(description = "Display name of location")
    String displayName;

    @NotBlank
    @Column(name = "lat")
    @Schema(description = "Latitude of location")
    Double latitude;

    @NotBlank
    @Column(name = "lon")
    @Schema(description = "Longitude of location")
    Double longitude;

    public static LocationEntity from(LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDisplayName(locationDTO.getDisplayName());
        locationEntity.setLatitude(locationDTO.getLatitude());
        locationEntity.setLongitude(locationDTO.getLongitude());
        return locationEntity;
    }
}
