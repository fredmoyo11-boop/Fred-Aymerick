package com.sep.backend.route;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.TripRequestEntity;
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

    @Schema(description = "The latitude.")
    private Double latitude;

    @Schema(description = "The longitude.")
    private Double longitude;

    public static Coordinate from(Location location) {
        var coordinate = new Coordinate();
        coordinate.setLatitude(location.getLatitude());
        coordinate.setLongitude(location.getLongitude());
        return coordinate;
    }

    public static Coordinate from(LocationEntity entity) {
        var coordinate = new Coordinate();
        coordinate.setLatitude(entity.getLatitude());
        coordinate.setLongitude(entity.getLongitude());
        return coordinate;
    }

    public static Coordinate from(NominatimFeature feature) {
        var coordinate = new Coordinate();
        double  lat = feature.getGeometry().getCoordinates().getFirst();
        double  lon = feature.getGeometry().getCoordinates().getLast();
        coordinate.setLatitude(lat);
        coordinate.setLongitude(lon);
        return coordinate;
    }
}
