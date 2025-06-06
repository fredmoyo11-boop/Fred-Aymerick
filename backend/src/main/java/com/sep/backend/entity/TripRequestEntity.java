package com.sep.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TripRequestEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)  // Achtung: Funktioniert nur mit bestimmten JPA-Implementierungen
    private CustomerEntity customer;

    @OneToOne
    @JoinColumn(name = "route_id", nullable = false)
    private RouteEntity route;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @OneToMany(mappedBy = "tripRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripOfferEntity> offers = new ArrayList<>();

    @Column(name = "car_type", nullable = false)
    private String carType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "price", nullable = false)
    private Double price; // in euro

    @Column(name = "note", nullable = false)
    private String note;
}
