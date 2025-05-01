package com.sep.backend.entity;

import com.sep.backend.requestDrive.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Auditable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public abstract class RequestFormEntity extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private float formID;

    //UserID foreign key

    @NotBlank
    @Column(name = "first_name")
    private String firstname;

    @NotBlank
    @Column(name = "last_name")
    private String lastname;

    @NotBlank
    @Column(name = "notes")
    private String notes;

    //StartLocation -> Location Entity?

    //EndLocation

    @NotBlank
    @Column(name = "status")
    private String requestStatus;

    @NotBlank
    @Column(name = "car_type")
    private String cartype;
}
