package com.sep.backend.location;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.nominatim.data.NominatimFeature;
import com.sep.backend.route.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Location {

    @Schema(description = "The id of the location. Might be null if location does not have related entity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long locationId;

    @NotNull
    @Schema(description = "The coordinate of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Coordinate coordinate;

    @NotBlank
    @Schema(description = "The display name of the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @NotNull
    @Schema(description = "The Nominatim GeoJSON for the location.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NominatimFeature geoJSON;

    public static Location from(NominatimFeature feature) {
        var location = new Location();

        var nominatimGeometry = feature.getGeometry();
        if (!"Point".equals(nominatimGeometry.getType())) {
            throw new IllegalArgumentException("Geometry type must be Point");
        }

        var coordinate = Coordinate.from(feature);
        location.setCoordinate(coordinate);

        var nominatimProperties = feature.getProperties();
        location.setDisplayName(nominatimProperties.getDisplayName());

        location.setGeoJSON(feature);

        return location;
    }

    public static Location from(LocationEntity entity) {
        var location = new Location();
        location.setLocationId(entity.getId());
        var coordinate = Coordinate.from(entity);
        location.setCoordinate(coordinate);
        location.setDisplayName(entity.getDisplayName());
        location.setGeoJSON(entity.getGeoJSON());
        return location;
    }

}
