package com.sep.backend.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripHistoryRepository extends JpaRepository<TripHistoryEntity, Long> {

    List<TripHistoryEntity>findByCustomer(CustomerEntity customer);

    List<TripHistoryEntity>findByDriver(DriverEntity driverEntity);

    boolean existsByDriver(DriverEntity driverEntity);

    boolean existsByCustomer(CustomerEntity customerEntity);
}
