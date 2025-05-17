package com.sep.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
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

    @JsonProperty("display_name")
    String displayName;

    @JsonProperty("lat")
    Double latitude;

    @JsonProperty("lon")
    Double longitude;

    public static LocationEntity from(@Valid LocationDTO locationDTO) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setDisplayName(locationDTO.getDisplayName());
        locationEntity.setLatitude(locationDTO.getLatitude());
        locationEntity.setLongitude(locationDTO.getLongitude());
        return locationEntity;
    }
}
