package com.sep.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"trip_request_id", "driver_id"}))
public class TripOfferEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "trip_request_id", nullable = false)
    private TripRequestEntity tripRequest;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;

    @Column(name = "status", nullable = false)
    private String status;
}
