package com.sep.backend.requestDrive.nominatim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter

public class Location {
    @JsonProperty("place_id")
    long placeID;

    @JsonProperty("display_name")
    String displayName;

    @JsonProperty("address")
    List<String> address;

    @JsonProperty("lat")
    Double latitude;

    @JsonProperty("lon")
    Double longitude;
}
