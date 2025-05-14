package com.sep.backend.triprequest;

import com.sep.backend.entity.TripRequestEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequestEntity, Long> {

    Optional<TripRequestEntity> findById(Long id);

    Optional<TripRequestEntity> findByCustomer_Email(String email);

    boolean existsByCustomer_EmailAndRequestStatus(String email, String status);

}
