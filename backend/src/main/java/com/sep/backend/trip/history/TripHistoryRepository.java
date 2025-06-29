package com.sep.backend.trip.history;

import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.entity.TripHistoryEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT sum(?2) FROM TripHistoryEntity WHERE driver.id = ?1 AND ")
    List<Number> getSumStatisticsByDriver_Email(@NotBlank String driverId, @NotBlank String type, @NotBlank LocalDateTime startTime, @NotBlank LocalDateTime endTime);
}
