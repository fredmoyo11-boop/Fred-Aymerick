package com.sep.backend.trip.offer;

import com.sep.backend.entity.TripOfferEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripOfferRepository extends JpaRepository<TripOfferEntity, Long> {
    Optional<TripOfferEntity> findByDriver_EmailAndTripRequest_Id(@NotBlank String driver_email, @NotBlank Long trip_request_id);

    boolean existsByDriver_EmailAndStatus(@Email @NotBlank String email, @NotBlank String status);

    List<TripOfferEntity> findAllByTripRequest_Customer_EmailAndStatus(@Email @NotBlank String tripRequestCustomerEmail, @NotBlank String status);

    List<TripOfferEntity> findByTripRequest_IdAndStatus(Long tripRequestId, String status);

    List<TripOfferEntity> findByDriver_EmailAndStatus(@Email @NotBlank String driverUsername, @NotBlank String status);
}
