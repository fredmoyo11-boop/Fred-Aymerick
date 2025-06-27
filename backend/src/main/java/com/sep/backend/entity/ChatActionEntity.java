package com.sep.backend.entity;

import com.sep.backend.trip.offer.chat.action.ChatActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "chat_action")
public class ChatActionEntity extends DurableEntity {

    @ManyToOne
    @JoinColumn(name = "chat_message_id", nullable = false)
    private ChatMessageEntity chatMessage;

    @ChatActionType
    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "content", length = 10000)
    private String content;

}
