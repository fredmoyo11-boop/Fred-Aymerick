package com.sep.backend.entity;

import com.sep.backend.CarTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "driver")
@Entity
public non-sealed class DriverEntity extends AccountEntity {
    // add additional fields that only belong to driver
    @Column(name = "car_type")
    private String carType = CarTypes.MEDIUM;

    @OneToMany(mappedBy = "driver")
    private List<TripHistoryEntity> tripHistories = new ArrayList<>();


}
