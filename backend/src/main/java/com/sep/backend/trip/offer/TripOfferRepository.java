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

//    @Query("SELECT new com.sep.backend.trip.offer.response.TripOfferResponse(d.username, d.firstName, d.lastName, avg(th.driverRating), count(th.tripOfferId), sum(th.distance)) FROM CustomerEntity c RIGHT JOIN TripRequestEntity tr RIGHT JOIN TripOfferEntity to LEFT JOIN DriverEntity d LEFT JOIN TripHistoryEntity th WHERE ?1=c.email ORDER BY ?2 ?3")
//    public List<TripOfferResponse> findTripOfferResponseByCustomer_Email(String email, String sort, String sortOrder);
}
