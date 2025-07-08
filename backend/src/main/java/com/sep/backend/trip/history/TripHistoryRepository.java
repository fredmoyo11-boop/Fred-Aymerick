package com.sep.backend.trip.history;

import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.entity.TripHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripHistoryRepository extends JpaRepository<TripHistoryEntity, Long> {

//    List<TripHistoryEntity> findByCustomer(CustomerEntity customer);
////
//    List<TripHistoryEntity>findByDriver(DriverEntity driverEntity);

    List<TripHistoryEntity> findByCustomer_Id(Long id);

    List<TripHistoryEntity> findByDriver_Id(Long id);


    boolean existsByDriver(DriverEntity driverEntity);

    boolean existsByCustomer(CustomerEntity customerEntity);

    Optional<TripHistoryEntity> findByTripOfferId(Long tripOfferId);

    List<TripHistoryEntity> findByDriver_Email(String email);
}
