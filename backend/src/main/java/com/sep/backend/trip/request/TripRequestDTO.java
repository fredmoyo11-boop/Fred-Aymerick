package com.sep.backend.trip.request;

import com.sep.backend.entity.TripRequestEntity;
import com.sep.backend.location.Location;
import com.sep.backend.route.RouteDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a request for a drive.")
public class TripRequestDTO {

    @Schema(description = "Unique id of trip request.", requiredMode = RequiredMode.REQUIRED)
    private Long tripRequestId;

    @Schema(description = "The customer requesting drive.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "The route of the trip request.", requiredMode = RequiredMode.REQUIRED)
    private RouteDTO route;

    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "The optional notes for the driver.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;

    @Schema(description = "The current status of the trip. Either ACTIVE or DELETED.", requiredMode = RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "The price for the trip request.", requiredMode = RequiredMode.REQUIRED)
    private Double price;

    public static TripRequestDTO from(TripRequestEntity tripRequestEntity) {
        var dto = new TripRequestDTO();
        var routeDTO = RouteDTO.from(tripRequestEntity.getRoute());

        dto.setTripRequestId(tripRequestEntity.getId());
        dto.setEmail(tripRequestEntity.getCustomer().getUsername());
        dto.setRoute(routeDTO);
        dto.setCarType(tripRequestEntity.getCarType());
        dto.setStatus(tripRequestEntity.getStatus());
        dto.setNote(tripRequestEntity.getNote());
        dto.setPrice(tripRequestEntity.getPrice());
        return dto;
    }
}
