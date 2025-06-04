package com.sep.backend.location;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.nominatim.data.NominatimFeature;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Location {

    @Schema(description = "The id of the location. Might be null if location does not have related entity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private long locationId;

    @Schema(description = "The longitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Double longitude;

    @Schema(description = "The latitude of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @Schema(description = "The display name of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String displayName;

    @Schema(description = "The Nominatim GeoJSON for the location.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NominatimFeature geoJSON;



    public static Location from(NominatimFeature feature) {
        var location = new Location();

        var nominatimGeometry = feature.getGeometry();
        if (!"Point".equals(nominatimGeometry.getType())) {
            throw new IllegalArgumentException("Geometry type must be Point");
        }
        List<Double> coordinate = nominatimGeometry.getCoordinates();
        location.setLongitude(coordinate.getFirst());
        location.setLatitude(coordinate.getLast());

        var nominatimProperties = feature.getProperties();
        location.setDisplayName(nominatimProperties.getDisplayName());

        location.setGeoJSON(feature);

        return location;
    }

    public static Location from(LocationEntity entity) {
        var location = new Location();
        location.setLocationId(entity.getId());
        location.setLatitude(entity.getLatitude());
        location.setLongitude(entity.getLongitude());
        location.setDisplayName(entity.getDisplayName());
        location.setGeoJSON(entity.getGeoJSON());
        return location;
    }

}
