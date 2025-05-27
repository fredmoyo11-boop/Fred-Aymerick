package com.sep.backend.entity;

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
@Table(name = "customer")
@Entity
public non-sealed class CustomerEntity extends AccountEntity {
    // add additional fields that only belong to customer

    @OneToMany(mappedBy = "customer")
    private List<TripHistoryEntity> tripHistories = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<TripRequestEntity> tripRequest;
}
