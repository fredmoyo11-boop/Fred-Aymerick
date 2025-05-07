package com.sep.backend.triprequest;

import com.sep.backend.entity.TripRequestEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequestEntity, Long> {

    Optional<TripRequestEntity> findById(Long id);

    Optional<TripRequestEntity> findByCustomer_Username(String username);

    boolean existsByCustomer_Username(String username);

}
