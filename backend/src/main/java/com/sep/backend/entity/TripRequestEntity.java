package com.sep.backend.entity;


import com.sep.backend.triprequest.nominatim.LocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Schema(description = "Represents a request form by a customer.")
public class TripRequestEntity extends AbstractEntity{

    //relation in JPA -> Relation zu Customer hinzufügen
    @NotBlank
    @OneToOne
    @JoinColumn(name = "username")
    @Schema(description = "The customer who made the request.", requiredMode = RequiredMode.REQUIRED)
    private CustomerEntity customer;

    //StartLocation -> Location Entity?
    @NotBlank
    @OneToOne
    @JoinColumn(name = "display_name")
    @Schema(description = "The start location of the request.", requiredMode = RequiredMode.REQUIRED)
    private LocationEntity startLocation;

    //EndLocation
    @NotBlank
    @OneToOne
    @JoinColumn(name = "display_name")
    @Schema(description = "The end location of the request.", requiredMode = RequiredMode.REQUIRED)
    private LocationEntity endLocation;

    @Column(name = "notes")
    @Schema(description = "Optional note which the customer can add.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String notes;

    @NotBlank
    @Column(name = "car_type")
    @Schema(description = "The type of the car requested.", requiredMode = RequiredMode.REQUIRED)
    private String cartype;

    @NotBlank
    @Column(name = "status")
    @Schema(description = "The status of the request form.", requiredMode = RequiredMode.REQUIRED)
    private String requestStatus;

    //AcceptedByDriverEntity?
}
