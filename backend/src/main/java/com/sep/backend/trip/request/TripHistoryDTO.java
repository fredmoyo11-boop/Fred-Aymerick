package com.sep.backend.trip.request;


import com.sep.backend.entity.TripHistoryEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter

@Data
public class TripHistoryDTO {
    // treating the trip offer id as the trip id, because trip offer happened

    @Schema(description = "die Id von der fahrt",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tripId;

    @Schema(description = " Das Abschlussdatum und die Uhrzeit",requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = " Die gefahrene Distanz",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distance; // in m

    @Schema(description = "  Die Fahrtdauer",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration; // in s

    @Schema(description = "Das gezahlte oder erhaltene Geld",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price; // in euro

    @Schema(description = " Die Bewertung des Kundens ",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer driverRating = -1;

    @Schema(description = " Die Bewertung des Fahrers",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer customerRating = -1;

    @Schema(description = " Name des Kundens (Vor und Nachname ) ",requiredMode = Schema.RequiredMode.REQUIRED)
    private String  customerName;

    @Schema(description = "Benutzername des Kundens",requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerUsername;

    @Schema( description = "Name des Fahrers (Vor und Nachname)",requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverName;

    @Schema(description = "Benutzername des Fahrers ",requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverUsername;


    public static List<TripHistoryDTO> getTripHistoryDTO(List <TripHistoryEntity> tripHistoryEntity) {
        return tripHistoryEntity.stream().map(tripHistory -> {
                   TripHistoryDTO dto = new TripHistoryDTO();
                    dto.setTripId(tripHistory.getId());
                    dto.setEndTime(tripHistory.getEndTime());
                    dto.setDistance(tripHistory.getDistance());
                    dto.setDuration(tripHistory.getDuration());
                    dto.setPrice(tripHistory.getPrice());
                    dto.setCustomerRating(tripHistory.getCustomerRating());
                    dto.setCustomerName(tripHistory.getCustomer().getFirstName() + " " + tripHistory.getCustomer().getLastName());
                    dto.setCustomerUsername(tripHistory.getCustomer().getUsername());
                    dto.setDriverRating(tripHistory.getDriverRating());
                    dto.setDriverName(tripHistory.getDriver().getFirstName() + " " + tripHistory.getDriver().getLastName());
                    dto.setDriverUsername(tripHistory.getDriver().getUsername());
            return dto;
        }).toList();
    }
}
