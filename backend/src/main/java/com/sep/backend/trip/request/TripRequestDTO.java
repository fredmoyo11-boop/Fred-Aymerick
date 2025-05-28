package com.sep.backend.trip.request;

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
        var dto = new TripRequestDTO();
        dto.setEmail(tripRequestEntity.getCustomer().getUsername());
//        dto.setStartLocation(LocationDTO.from(tripRequestEntity.getStartLocation()));
//        dto.setEndLocation(LocationDTO.from(tripRequestEntity.getEndLocation()));
//        dto.setCarType(tripRequestEntity.getCarType());
//        dto.setNote(tripRequestEntity.getNote());
//        dto.setStatus(tripRequestEntity.getRequestStatus());
        return dto;
    }
}
