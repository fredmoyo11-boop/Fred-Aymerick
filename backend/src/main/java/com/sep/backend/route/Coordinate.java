package com.sep.backend.route;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.location.Location;
import com.sep.backend.nominatim.data.NominatimFeature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a coordinate.")
public class Coordinate {

    @Schema(description = "The latitude.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latitude;

    @Schema(description = "The longitude.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longitude;

    public static Coordinate from(Location location) {
        return location.getCoordinate();
    }

    public static Coordinate from(LocationEntity entity) {
        var coordinate = new Coordinate();
        coordinate.setLatitude(entity.getLatitude());
        coordinate.setLongitude(entity.getLongitude());
        return coordinate;
    }

    public static Coordinate from(NominatimFeature feature) {
        var coordinate = new Coordinate(); //Nominatim gibt lat, lon. ORS benutzt lon, lat
        double  lat = feature.getGeometry().getCoordinates().getLast();
        double  lon = feature.getGeometry().getCoordinates().getFirst();
        coordinate.setLatitude(lat);
        coordinate.setLongitude(lon);
        return coordinate;
    }
}
