package com.sep.backend.trip.offer;

import com.sep.backend.entity.TripOfferEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripOfferRepository extends JpaRepository<TripOfferEntity, Long> {
    public boolean existsByDriver_EmailAndStatus(@Email @NotBlank String email, @NotBlank String status);

    Optional<TripOfferEntity> findByDriver_Email(@Email @NotBlank String email);

    Optional<TripOfferEntity> findByDriver_UsernameAndTripRequest_Customer_Email(@NotBlank String driverUsername, @Email @NotBlank String email);

    List<TripOfferEntity> findAllByTripRequest_Customer_Email(@Email @NotBlank String tripRequestCustomerEmail);

    List<TripOfferEntity> findAllByTripRequest_Customer_EmailAndStatus(@Email @NotBlank String tripRequestCustomerEmail, @NotBlank String status);

    List<TripOfferEntity> findByTripRequest_IdAndStatus(Long tripRequestId, String status);

    List<TripOfferEntity> findByDriver_EmailAndStatus(@Email @NotBlank String driverUsername, @NotBlank String status);



    @Query("SELECT avg(customerRating) FROM TripHistoryEntity WHERE driver.id = ?1")
    Double getAvgRatingByDriver_TripHistory(@NotBlank Long driverId);

    @Query("SELECT count(tripOfferId) FROM TripHistoryEntity WHERE driver.id = ?1")
    Integer getTotalDriveCountByDriver_TripHistory(@NotBlank Long driverId);

    @Query("SELECT sum(distance) FROM TripHistoryEntity WHERE driver.id = ?1")
    Double getTotalDriveDistanceByDriver_TripHistory(@NotBlank Long driverId);

}
