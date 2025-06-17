package com.sep.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TripHistoryEntity extends AbstractEntity {
    // every trip offer has exactly one trip history
    @Column(name = "trip_id", nullable = false, unique = true)
    // treating the trip offer id as the trip id, because trip offer happened
    private Long tripOfferId;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "distance", nullable = false)
    private Double distance; // in m

    @Column(name = "duration", nullable = false)
    private Integer duration; // in s

    @Column(name = "price", nullable = false)
    private Double price; // in euro

    @Range(min = 1, max = 5)
    @Column(name = "driver_rating", nullable = false)
    private Integer driverRating = -1;

    @Range(min = 1, max = 5)
    @Column(name = "customer_rating", nullable = false)
    private Integer customerRating = -1;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private DriverEntity driver;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

}
