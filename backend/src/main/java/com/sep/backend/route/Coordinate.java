package com.sep.backend.route;


import com.sep.backend.entity.LocationEntity;
import com.sep.backend.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Represents a coordinate.")
public class Coordinate {

    @Schema(description = "The latitude of the coordinate.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double latitude;

    @Schema(description = "The longitude of the coordinate.", requiredMode = Schema.RequiredMode.REQUIRED)
    private double longitude;

    public double distanceTo(Coordinate coordinate) {
        return Math.sqrt(Math.pow(this.latitude - coordinate.latitude, 2) + Math.pow(this.longitude - coordinate.longitude, 2));
    }

    public static Coordinate from(List<Double> coordinateList) {
        if (coordinateList.size() != 2) {
            throw new IllegalArgumentException("Coordinate must have exactly 2 elements.");
        }
        var coordinate = new Coordinate();
        coordinate.setLongitude(coordinateList.getFirst());
        coordinate.setLatitude(coordinateList.getLast());
        return coordinate;
    }

    public static Coordinate from(LocationEntity locationEntity) {
        var coordinate = new Coordinate();
        coordinate.setLongitude(locationEntity.getLongitude());
        coordinate.setLatitude(locationEntity.getLatitude());
        return coordinate;
    }

    public static Coordinate from(Location location) {
        return location.getCoordinate();
    }

}
