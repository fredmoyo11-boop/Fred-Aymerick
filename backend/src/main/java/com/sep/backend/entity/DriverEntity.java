package com.sep.backend.entity;

import com.sep.backend.account.Vehicleclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "driver")
@Entity
public class DriverEntity extends AccountEntity {
    Vehicleclass vehicleclass;
}
