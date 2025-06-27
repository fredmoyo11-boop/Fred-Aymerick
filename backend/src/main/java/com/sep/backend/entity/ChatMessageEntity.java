package com.sep.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_message")
public class ChatMessageEntity extends DurableEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_offer_id", nullable = false)
    private TripOfferEntity tripOffer;

    @Column(name = "direction", nullable = false)
    private String direction;

    @Column(name = "created_timestamp", nullable = false)
    private LocalDateTime createdTimestamp;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    @Column(name = "seen", nullable = false)
    private boolean seen = false;

    @Column(name = "edited", nullable = false)
    private boolean edited = false;

    @OneToMany(mappedBy = "chatMessage")
    private List<ChatActionEntity> chatActions = new ArrayList<>();

}
