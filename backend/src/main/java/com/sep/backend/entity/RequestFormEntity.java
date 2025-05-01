package com.sep.backend.entity;

import com.sep.backend.requestDrive.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class RequestFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "form_id")
    private float formID;

    @NotBlank
    @Column(name = "first_name")
    private String firstname;

    @NotBlank
    @Column(name = "last_name")
    private String lastname;

    @NotBlank
    @Column(name = "notes")
    private String notes;

    //StartLocation

    //EndLocation

    @NotBlank
    @Column(name = "status")
    private String requestStatus;

    @NotBlank
    @Column(name = "car_type")
    private String cartype;
}
