package com.sep.backend.trip.request;

import com.sep.backend.entity.TripRequestEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema( description = "Information in the table of AvailableTripRequest",requiredMode= Schema.RequiredMode.REQUIRED )
public class AvailableTripRequestDTO {


    @Schema(description = "the id of the TripRequest", requiredMode = Schema.RequiredMode.REQUIRED)
    private  Long Id;

    @Schema(description = "The creation time of the entity.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creationTime;

    @Schema(description = "The creation time of the entity.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String creationDate;

    @Schema( description = "username of the customer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema( description = "Note for the trip", requiredMode = Schema.RequiredMode.REQUIRED)
    private String note ;

    @Schema( description = " The  Car-Type for the trip which  the customer want ", requiredMode = Schema.RequiredMode.REQUIRED)
    private  String carType ;

    @Schema( description = "  Entfernung zwischen der aktuellen Position des Fahrers und dem Startpunkt der Fahrt", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double distance;



}
