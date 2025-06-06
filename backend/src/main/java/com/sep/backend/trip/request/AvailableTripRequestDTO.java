package com.sep.backend.trip.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Schema(description = "requestId der Fahrt ",implementation = Long.class,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long requestId;

    @Schema(description = " Erstellungsdatum und-uhrzeit der Fahranfrage ",implementation =LocalDateTime.class,requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestTime;

    @Schema(description = " Kundenname",implementation = String.class,  requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerUsername;

    @Schema(description = "Kundenbewertung", implementation =Double.class,requiredMode = Schema.RequiredMode.REQUIRED)
    private Double  customerRating;

    @Schema(description = " Gewünschte Fahrzeugklasse",implementation = String.class,requiredMode = Schema.RequiredMode.REQUIRED)
    private String desiredCarType;

    @Schema(description = "Entfernung zwischen der aktuellen Position des Fahrers und dem Startpunkt der Fahrt",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distanceInKm;

    @Schema(description = "Gesamte Distance der Fahrt ",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalDistanceInKm;

    @Schema(description = "Gesamte Preis der Fahrt ",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "Gesamte Dauer der Fahrt in Sekunde ",requiredMode = Schema.RequiredMode.REQUIRED)
    private Double duration;


}

