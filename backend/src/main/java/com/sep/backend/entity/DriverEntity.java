package com.sep.backend.entity;

import com.sep.backend.account.CarType;
import com.sep.backend.account.CarType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "driver")
@Entity
public class DriverEntity extends AccountEntity {
    private CarType carType;
}
