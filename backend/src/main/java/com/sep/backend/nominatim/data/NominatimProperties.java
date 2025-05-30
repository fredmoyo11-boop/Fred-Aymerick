package com.sep.backend.nominatim.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Represents the properties of a nominatim feature.")
public class NominatimProperties {

    @JsonProperty("place_id")
    @Schema(description = "The place id of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String placeId;

    @JsonProperty("osm_type")
    @Schema(description = "The osm type of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String osmType;

    @JsonProperty("osm_id")
    @Schema(description = "The osm id of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String osmId;

    @JsonProperty("place_rank")
    @Schema(description = "The place rank of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String placeRank;

    @JsonProperty("category")
    @Schema(description = "The category of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @JsonProperty("type")
    @Schema(description = "The type of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @JsonProperty("importance")
    @Schema(description = "The importance of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double importance;

    @JsonProperty("addresstype")
    @Schema(description = "The address type of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String addresstype;

    @JsonProperty("name")
    @Schema(description = "The name of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @JsonProperty("display_name")
    @Schema(description = "The display name of the feature.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;
}
