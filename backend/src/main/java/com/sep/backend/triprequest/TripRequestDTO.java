package com.sep.backend.triprequest;

import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.triprequest.nominatim.data.LocationDTO;
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

    @NotBlank
    @Schema(description = "The customer requesting drive.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @NotNull
    @Schema(description = "The start location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private LocationDTO startLocation;

    @NotNull
    @Schema(description = "The end location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private LocationDTO endLocation;

    @NotBlank
    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "Optional notes by customer", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;

    public static TripRequestDTO from(TripRequestEntity tripRequestEntity) {
        var dto = new TripRequestDTO();
        dto.setEmail(tripRequestEntity.getCustomer().getUsername());
        dto.setStartLocation(LocationDTO.from(tripRequestEntity.getStartLocation()));
        dto.setEndLocation(LocationDTO.from(tripRequestEntity.getEndLocation()));
        dto.setCarType(tripRequestEntity.getCartype());
        dto.setNote(tripRequestEntity.getNote());
        return dto;
    }
}
