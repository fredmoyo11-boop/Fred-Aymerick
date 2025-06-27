package com.sep.backend.account.Leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema( description = "FahrerLeaderboard",requiredMode = Schema.RequiredMode.REQUIRED )
public class Leaderboard {

    @Schema(description="username des Fahrers",requiredMode= Schema.RequiredMode.REQUIRED)
    private String driverUsername;

    @Schema(description = "Name des Fahrers(Vor- und Nachname",requiredMode = Schema.RequiredMode.REQUIRED)
    private String driverName;

    @Schema(description ="Gesamte gefahrene Distance",requiredMode= Schema.RequiredMode.REQUIRED)
    private double totalDrivenDistance;

    @Schema(description="Gesamte Fahrzeit",requiredMode= Schema.RequiredMode.REQUIRED)
    private double averageRating;

    @Schema(description="Gesamte Fahrzeit",requiredMode= Schema.RequiredMode.REQUIRED)
    private double totalDriveTime;

    @Schema(description="Gesamte Anzahl der gefahrene Trips",requiredMode= Schema.RequiredMode.REQUIRED)
    private Integer totalNumberOfDrivenTrip;

    @Schema(description ="Total verdientes Geld",requiredMode= Schema.RequiredMode.REQUIRED)
    private Double totalEarnings;
}
