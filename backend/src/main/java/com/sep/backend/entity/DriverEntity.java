package com.sep.backend.entity;

import com.sep.backend.account.CarType;
import com.sep.backend.account.CarType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "driver")
@Entity
public class DriverEntity extends AccountEntity {
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type", nullable = false)
    private CarType carType;
}
