package com.sep.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "driver")
@Entity
public class DriverEntity extends AccountEntity {
    @Column(name = "car_type", nullable = false)
    @NotBlank
    @Pattern(regexp = "MEDIUM|LARGE|DELUXE", message = "Invalid car type. Accepted values are: MEDIUM, LARGE, DELUXE.")
    private String carType;
}
