package com.sep.backend.trip.offer.chat;

import com.sep.backend.trip.offer.TripOfferService;
import com.sep.backend.trip.offer.chat.action.ChatActionTypes;
import com.sep.backend.trip.offer.chat.action.ChatTransactionService;
import com.sep.backend.trip.offer.chat.action.InboundChatAction;
import com.sep.backend.trip.offer.chat.message.ChatMessageRepository;
import com.sep.backend.trip.offer.chat.message.OutboundChatMessage;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatTransactionService chatTransactionService;
    private final TripOfferService tripOfferService;

    public ChatService(ChatMessageRepository chatMessageRepository, ChatTransactionService chatTransactionService, TripOfferService tripOfferService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatTransactionService = chatTransactionService;
        this.tripOfferService = tripOfferService;
    }

    public OutboundChatMessage handleChatAction(Long tripOfferId, @Valid InboundChatAction chatAction, Principal principal) {
        log.debug("Received chat action {} for trip offer {} from {}.", chatAction.getActionType(), tripOfferId, principal.getName());
        final String tripRole = tripOfferService.findRoleOfTrip(tripOfferId, principal)
                .orElseThrow(() -> new ChatException("User is not part of trip offer."));
        // if a trip role is present, it is either CUSTOMER or DRIVER
        log.debug("Principal is {} in this trip offer.", tripRole);

        final String actionType = chatAction.getActionType();
        final String content = chatAction.getContent();
        final Long chatMessageId = chatAction.getChatMessageId();
        log.debug("Received chat action {} with content {} for chat message {}.", actionType, content, chatMessageId);
        return switch (actionType) {
            case ChatActionTypes.SEND -> chatTransactionService.sendChatMessage(tripOfferId, content, tripRole);
            case ChatActionTypes.SEEN -> chatTransactionService.seenChatMessage(chatMessageId);
            case ChatActionTypes.EDIT -> chatTransactionService.editChatMessage(chatMessageId, content);
            case ChatActionTypes.DELETE -> chatTransactionService.deleteChatMessage(chatMessageId);
            default -> throw new ChatException("Unknown action type: " + actionType);
        };
    }

    public List<OutboundChatMessage> getAllMessages(Long tripOfferId, Principal principal) {
        log.debug("Received request to get all messages for trip offer {} from {}.", tripOfferId, principal.getName());
        if (!tripOfferService.isPartOfTrip(tripOfferId, principal)) {
            log.debug("User is not part of trip offer {}.", tripOfferId);
            throw new ChatException("User is not part of trip offer.");
        }
        log.debug("User is part of trip offer {}.", tripOfferId);
        return chatMessageRepository.findByTripOffer_IdOrderByCreatedTimestampAsc(tripOfferId).stream().map(OutboundChatMessage::from).toList();
    }
}
