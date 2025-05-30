package com.sep.backend.trip.request;

import com.sep.backend.entity.LocationEntity;
import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.location.Location;
import com.sep.backend.ors.data.ORSFeatureCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a request for a drive.",requiredMode = RequiredMode.REQUIRED)
public class TripRequestDTO {

    @Schema(description = "Eindeutige ID der Fahranfrage",requiredMode = RequiredMode.REQUIRED)
    private Long tripRequestId;

    @Schema(description = "Zeitpunkt der Anfrage", requiredMode = RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(description = "The customer requesting drive.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "The start location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private Location startLocation;

    @Schema(description = "The end location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private Location endLocation;

    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String desiredCarType;

    @Schema(description = "Die Optionale Zwischenstops während der Fahrt ", requiredMode = RequiredMode.NOT_REQUIRED)
    private List<Location> stops ;

    @Schema(description = "The optional notes for the driver.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;

    @Schema(description = "The current status of the trip. Either ACTIVE or DELETED.")
    private String status;

    @Schema(description = "Die Gesamte Route von der Fahrt", requiredMode = RequiredMode.REQUIRED)
    private ORSFeatureCollection geoJson;


    public static TripRequestDTO from(TripRequestEntity tripRequestEntity) {
        Location startLocation = getLocation(tripRequestEntity.getRoute().getStartLocation());

        Location endLocation = getLocation(tripRequestEntity.getRoute().getEndLocation());

        TripRequestDTO dto = new TripRequestDTO();
        dto.setEmail(tripRequestEntity.getCustomer().getEmail());
        dto.setTripRequestId(tripRequestEntity.getId());
        dto.setCreatedAt(tripRequestEntity.getRequestTime());
        dto.setStartLocation(startLocation);
        dto.setEndLocation(endLocation);
        dto.setNote(tripRequestEntity.getNote());
        dto.setDesiredCarType(tripRequestEntity.getDesiredCarType());
        dto.setStatus(tripRequestEntity.getStatus());
        dto.setGeoJson(tripRequestEntity.getRoute().getGeoJSON());
        dto.setEmail(tripRequestEntity.getCustomer().getEmail());
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
