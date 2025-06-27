package com.sep.backend.trip.offer.chat;

import com.sep.backend.trip.offer.chat.action.InboundChatAction;
import com.sep.backend.trip.offer.chat.message.OutboundChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatSocketController {

    private final ChatService chatService;

    public ChatSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat/{tripOfferId}")
    @SendTo("/topic/trip/{tripOfferId}")
    public OutboundChatMessage handleChatAction(@DestinationVariable Long tripOfferId, @Payload InboundChatAction inboundChatAction, Principal principal) {
        return chatService.handleChatAction(tripOfferId, inboundChatAction, principal);
    }

}
