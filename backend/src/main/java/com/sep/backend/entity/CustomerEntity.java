package com.sep.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
@Entity
public class CustomerEntity extends AccountEntity {
    // add additional fields that only belong to customer
}
