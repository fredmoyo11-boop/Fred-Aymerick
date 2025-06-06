package com.sep.backend.notification;

import com.sep.backend.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByCustomer_Email(String customerEmail);

    List<NotificationEntity> findAllByDriver_Email(String driverEmail);
}
