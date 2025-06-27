package com.sep.backend.trip.offer.chat.message;

import com.sep.backend.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findByTripOffer_IdOrderByCreatedTimestampAsc(Long tripOfferId);
}
