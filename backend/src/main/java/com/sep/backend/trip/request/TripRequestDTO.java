package com.sep.backend.trip.request;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a request for a drive.")
public class TripRequestDTO {

    @Schema(description = "The customer requesting drive.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "The start location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private Location startLocation;

    @Schema(description = "The end location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private Location endLocation;

    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The optional notes for the driver.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;

    @Schema(description = "The current status of the trip. Either ACTIVE or DELETED.")
    private String status;

    public static TripRequestDTO from(TripRequestEntity tripRequestEntity) {
        Location startLocation = getLocation(tripRequestEntity.getRoute().getStartLocation());

        Location endLocation = getLocation(tripRequestEntity.getRoute().getEndLocation());

        TripRequestDTO dto = new TripRequestDTO();
        dto.setEmail(tripRequestEntity.getCustomer().getEmail());
        dto.setStartLocation(startLocation);
        dto.setEndLocation(endLocation);
        dto.setNote(tripRequestEntity.getNote());
        dto.setCarType(tripRequestEntity.getDesiredCarType());
        dto.setStatus(tripRequestEntity.getStatus());
        return dto;
    }

    public  static Location getLocation(LocationEntity locationEntity) {
        Location location = new Location();
        location.setLocationId(locationEntity.getId());
        location.setDisplayName(locationEntity.getDisplayName());
        location.setLongitude(locationEntity.getLongitude());
        location.setLatitude(locationEntity.getLatitude());
        location.setGeoJSON(locationEntity.getGeoJSON());
        return location;
    }
}
