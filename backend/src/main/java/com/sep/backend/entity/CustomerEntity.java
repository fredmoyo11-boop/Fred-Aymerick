package com.sep.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
@Entity
public class CustomerEntity extends AccountEntity {
    // add additional fields that only belong to customer

    @OneToMany(mappedBy = "customer")
    private List<TripRequestEntity> tripRequests = new ArrayList<>();

}
