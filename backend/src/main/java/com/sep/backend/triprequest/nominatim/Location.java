package com.sep.backend.triprequest.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @JsonProperty("place_id")
    long placeID;

    @JsonProperty("display_name")
    String displayName;

    @JsonProperty("lat")
    Double latitude;

    @JsonProperty("lon")
    Double longitude;
}
