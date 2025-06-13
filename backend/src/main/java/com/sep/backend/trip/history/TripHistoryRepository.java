package com.sep.backend.trip.history;

import com.sep.backend.entity.TripHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripHistoryRepository extends JpaRepository<TripHistoryEntity, Long> {

    Optional<TripHistoryEntity> findByTripOfferId(Long tripOfferId);
}
