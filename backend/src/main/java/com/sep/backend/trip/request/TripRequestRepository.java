package com.sep.backend.trip.request;

import com.sep.backend.entity.TripRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequestEntity, Long> {


    Optional<TripRequestEntity> findByCustomer_Email(String email);

    Optional<TripRequestEntity> findByCustomer_EmailAndStatus(String email, String status);

    List<TripRequestEntity> findByStatus(String active);


    boolean existsByCustomer_EmailAndStatus(String email, String requestStatus);
}
