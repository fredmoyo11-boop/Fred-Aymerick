package com.sep.backend.trip.offer;

import com.sep.backend.entity.TripOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TripOfferRepository  extends JpaRepository<TripOfferEntity, Long> {

}
