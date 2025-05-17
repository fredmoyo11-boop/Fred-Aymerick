package com.sep.backend.trip.request;

import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.trip.nominatim.data.LocationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private LocationDTO startLocation;

    @Schema(description = "The end location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private LocationDTO endLocation;

    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "Optional notes by customer", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;

    @Schema(description = "Shows current status. Either ACTIVE, INPROGRESS or COMPLETED")
    private String status;

    public static TripRequestDTO from(TripRequestEntity tripRequestEntity) {
        var dto = new TripRequestDTO();
        dto.setEmail(tripRequestEntity.getCustomer().getUsername());
        dto.setStartLocation(LocationDTO.from(tripRequestEntity.getStartLocation()));
        dto.setEndLocation(LocationDTO.from(tripRequestEntity.getEndLocation()));
        dto.setCarType(tripRequestEntity.getCartype());
        dto.setNote(tripRequestEntity.getNote());
        dto.setStatus(tripRequestEntity.getRequestStatus());
        return dto;
    }
}
