package com.sep.backend.trip.offer.chat.action;

import com.sep.backend.entity.ChatActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatActionRepository extends JpaRepository<ChatActionEntity, Long> {

}
