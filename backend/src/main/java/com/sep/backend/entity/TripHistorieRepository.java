package com.sep.backend.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TripHistorieRepository extends JpaRepository<TripRequestEntity, Long> {

    List<TripHistoryEntity>findByCustomer(CustomerEntity customer);
}
