package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.backend.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}
