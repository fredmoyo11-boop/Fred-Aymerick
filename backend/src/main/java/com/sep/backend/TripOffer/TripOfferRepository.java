package com.sep.backend.TripOffer;

import com.sep.backend.entity.TripOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripOfferRepository extends JpaRepository<TripOfferEntity,Long> {

}
