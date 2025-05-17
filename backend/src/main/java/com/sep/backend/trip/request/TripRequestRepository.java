package com.sep.backend.trip.request;

import com.sep.backend.entity.TripRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequestEntity, Long> {

    Optional<TripRequestEntity> findById(Long id);

    Optional<TripRequestEntity> findByCustomer_Email(String email);

    boolean existsByCustomer_EmailAndRequestStatus(String email, String status);

//    Optional<TripRequestEntity> findByCustomer_EmailAndRequestStatus(String customerEmail, String requestStatus);
   List<TripRequestEntity> findByCustomer_EmailAndRequestStatus(String customerEmail, String requestStatus);
    
    Optional<TripRequestEntity> findByCustomerEmail(String email);
}
