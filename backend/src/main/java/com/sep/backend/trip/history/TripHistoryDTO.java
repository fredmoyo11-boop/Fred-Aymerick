package com.sep.backend.trip.history;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Schema(description = " Trip-history eines Fahrers oder Kunden ", requiredMode = Schema.RequiredMode.REQUIRED)
public class TripHistoryDTO {

    @Schema(description = "die Id von der fahrt",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long tripId;

    @Schema(description = " Das Abschlussdatum und die Uhrzeit",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull
    private LocalDateTime endTime;

    @Schema(description = " Die gefahrene Distanz",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Double distance;

    @Schema(description = "  Die Fahrtdauer",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer duration;

    @Schema(description = "Das gezahlte oder erhaltene Geld",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Double price;

    @Schema(description = " Die Bewertung des Kundens ",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer driverRating ;

    @Schema(description = " Die Bewertung des Fahrers",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer customerRating ;

    @Schema(description = " Name des Kundens (Vor und Nachname ) ",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String  customerName;

    @Schema(description = "Benutzername des Kundens",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String customerUsername;

    @Schema( description = "Name des Fahrers (Vor und Nachname)",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String driverName;

    @Schema(description = "Benutzername des Fahrers ",requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String driverUsername;
}
