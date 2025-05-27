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
@Table(name = "transaction")
public class TransactionEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    // might be null if the transaction is DEPOSIT or WITHDRAW
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    // might be null if the transaction is DEPOSIT or WITHDRAW
    private DriverEntity driver;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "amount", nullable = false)
    private Double amount;

}
