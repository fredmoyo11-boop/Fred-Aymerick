package com.sep.backend.trip.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Schema(description = " Tabelle-Informationen der verfügbaren Fahranfragen ", requiredMode = Schema.RequiredMode.REQUIRED)

public class AvailableTripRequestDTO {

    @Schema(description = "requestId der Fahrt ",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long requestId;

    @Schema(description = " Erstellungsdatum und-uhrzeit der Fahranfrage ",requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime requestTime;

    @Schema(description = " Kundenname",  requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerUsername;

    @Schema(description = "Kundenbewertung", requiredMode = Schema.RequiredMode.REQUIRED)
    private double customerRating;

    @Schema(description = " Gewünschte Fahrzeugklasse",requiredMode = Schema.RequiredMode.REQUIRED)
    private String desiredCarType;

    @Schema(description = "Entfernung zwischen der aktuellen Position des Fahrers und dem Startpunkt der Fahrt",requiredMode = Schema.RequiredMode.REQUIRED)
    private double distanceInKm;

}

