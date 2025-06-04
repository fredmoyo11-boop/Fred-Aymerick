package com.sep.backend.entity;

import com.sep.backend.notification.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
@Entity
public class NotificationEntity extends AbstractEntity {

    @NotNull
    @NotificationType
    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @NotBlank
    private String message;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    // Might be null if the notification is for a driver
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    // Might be null if the notification is for a customer
    private DriverEntity driver;
}
