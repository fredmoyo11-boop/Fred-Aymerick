package com.sep.backend.route;

import com.sep.backend.entity.WaypointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaypointRepository extends JpaRepository<WaypointEntity, Long> {

}