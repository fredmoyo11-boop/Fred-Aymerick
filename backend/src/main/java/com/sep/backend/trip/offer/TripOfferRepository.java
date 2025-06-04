package com.sep.backend.trip.offer;

import com.sep.backend.entity.TripOfferEntity;
import com.sep.backend.entity.TripHistoryEntity;
import com.sep.backend.entity.DriverEntity;
import com.sep.backend.trip.offer.response.TripOfferResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;

@Repository
public interface TripOfferRepository  extends JpaRepository<TripOfferEntity, Long> {
    public boolean existsByDriver_EmailAndStatus(String email, String status);

    List<TripOfferEntity> findAllByTripRequest_Customer_Email(@Email @NotBlank String tripRequestCustomerEmail);
	
}
