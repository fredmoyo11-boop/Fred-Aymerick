package com.sep.backend.trip.request;

import com.sep.backend.entity.DurableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DurableRepository extends JpaRepository<DurableEntity, Long> {

}
