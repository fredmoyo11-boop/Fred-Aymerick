package com.sep.backend.triprequest;

import com.sep.backend.entity.TripRequestEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequestEntity, Long> {

    Optional<TripRequestEntity> findByRequestID(@NotBlank float requestID);

    Optional<TripRequestEntity> findByUsername(@NotBlank String username);

}
