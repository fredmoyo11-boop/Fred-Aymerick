package com.sep.backend.trip.offer.chat.action;

import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.entity.ChatActionEntity;
import com.sep.backend.entity.ChatMessageEntity;
import com.sep.backend.trip.offer.TripOfferService;
import com.sep.backend.trip.offer.chat.ChatException;
import com.sep.backend.trip.offer.chat.message.ChatMessageDirections;
import com.sep.backend.trip.offer.chat.message.ChatMessageRepository;
import com.sep.backend.trip.offer.chat.message.OutboundChatMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ChatTransactionService {

    private final ChatActionRepository chatActionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TripOfferService tripOfferService;

    public ChatTransactionService(ChatActionRepository chatActionRepository, ChatMessageRepository chatMessageRepository, TripOfferService tripOfferService) {
        this.chatActionRepository = chatActionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.tripOfferService = tripOfferService;
    }

    @Transactional
    public OutboundChatMessage sendChatMessage(Long tripOfferId, String content, String tripRole) throws NotFoundException {
        log.debug("SEND: Sending chat message for trip offer {} with content {} and role {}.", tripOfferId, content, tripRole);
        if (content == null || content.isBlank()) {
            log.debug("SEND: Content is null or blank. Content: '{}'", content);
            throw new ChatException("Content must not be null or blank.");
        }
        log.debug("SEND: Content is not null or blank. Content: '{}'", content);

        var direction = switch (tripRole) {
            case Roles.CUSTOMER -> ChatMessageDirections.C2D;
            case Roles.DRIVER -> ChatMessageDirections.D2C;
            default -> throw new IllegalStateException("Unexpected value: " + tripRole);
        };
        log.debug("SEND: Trip role is {}. Direction: {}", tripRole, direction);

        var tripOfferEntity = tripOfferService.getTripOffer(tripOfferId);

        log.debug("SEND: Saving new chat message and chat action for trip offer {} with content {} and direction {}.", tripOfferId, content, direction);
        var chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setTripOffer(tripOfferEntity);
        chatMessageEntity.setDirection(direction);
        var timestamp = LocalDateTime.now();
        chatMessageEntity.setCreatedTimestamp(timestamp);
        chatMessageEntity.setTimestamp(timestamp);
        chatMessageEntity.setContent(content);
        chatMessageRepository.save(chatMessageEntity);

        var chatActionEntity = new ChatActionEntity();
        chatActionEntity.setChatMessage(chatMessageEntity);
        chatActionEntity.setActionType(ChatActionTypes.SEND);
        chatActionEntity.setTimestamp(timestamp);
        chatActionEntity.setContent(content);
        chatActionRepository.save(chatActionEntity);
        log.debug("SEND: Saved new chat message and chat action for trip offer {} with content {} and direction {}.", tripOfferId, content, direction);

        return OutboundChatMessage.from(chatMessageRepository.save(chatMessageEntity));
    }

    @Transactional
    public OutboundChatMessage seenChatMessage(Long chatMessageId) throws NotFoundException {
        log.debug("SEEN: Checking if chat message with id {} exists.", chatMessageId);
        var chatMessageEntity = getChatMessage(chatMessageId);
        log.debug("SEEN: Chat message with id {} exists.", chatMessageId);

        log.debug("SEEN: Setting seen flag for chat message with id {}.", chatMessageId);
        chatMessageEntity.setSeen(true);
        var timestamp = LocalDateTime.now();
        log.debug("SEEN: Setting timestamp for chat message with id {} to {}.", chatMessageId, timestamp);
        chatMessageEntity.setTimestamp(timestamp);
        chatMessageRepository.save(chatMessageEntity);
        log.debug("SEEN: Updated chat message with id {}", chatMessageId);

        log.debug("SEEN: Creating seen chat action for chat message with id {}.", chatMessageId);
        var chatActionEntity = new ChatActionEntity();
        chatActionEntity.setChatMessage(chatMessageEntity);
        chatActionEntity.setActionType(ChatActionTypes.SEEN);
        chatActionEntity.setTimestamp(timestamp);
        chatActionRepository.save(chatActionEntity);
        log.debug("SEEN: Created seen chat action for chat message with id {}.", chatMessageId);

        return OutboundChatMessage.from(chatMessageRepository.save(chatMessageEntity));
    }

    @Transactional
    public OutboundChatMessage editChatMessage(Long chatMessageId, String content) throws NotFoundException {
        log.debug("EDIT: Checking if chat message with id {} exists.", chatMessageId);
        var chatMessageEntity = getChatMessage(chatMessageId);
        log.debug("EDIT: Chat message with id {} exists.", chatMessageId);

        log.debug("EDIT: Checking if chat message with id {} has been deleted.", chatMessageId);
        if (chatMessageEntity.isDeleted()) {
            log.debug("EDIT: Chat message with id {} has been deleted.", chatMessageId);
            throw new ChatException("Cannot edit message that has been deleted.");
        }
        log.debug("EDIT: Chat message with id {} has not been deleted.", chatMessageId);

        log.debug("EDIT: Checking if chat message with id {} has been seen.", chatMessageId);
        if (chatMessageEntity.isSeen()) {
            log.debug("EDIT: Chat message with id {} has been seen.", chatMessageId);
            throw new ChatException("Cannot edit message that has been seen.");
        }
        log.debug("EDIT: Chat message with id {} has not been seen.", chatMessageId);

        log.debug("EDIT: Checking if content is null or blank. Content: '{}'", content);
        if (content == null || content.isBlank()) {
            log.debug("EDIT: Content is null or blank. Content: '{}'", content);
            throw new ChatException("Edited content must not be null or blank.");
        }
        log.debug("EDIT: Content is not null or blank. Content: '{}'", content);

        // only edit a message if the content has changed
        log.debug("EDIT: Checking if content of chat message with id {} has changed.", chatMessageId);
        if (!chatMessageEntity.getContent().equals(content)) {
            log.debug("EDIT: Content of chat message with id {} has changed.", chatMessageId);
            log.debug("EDIT: Updating content of chat message with id {} to '{}'.", chatMessageId, content);
            chatMessageEntity.setContent(content);
            var timestamp = LocalDateTime.now();
            log.debug("EDIT: Updating timestamp of chat message with id {} to {}.", chatMessageId, timestamp);
            chatMessageEntity.setTimestamp(timestamp);
            chatMessageEntity.setEdited(true);
            chatMessageRepository.save(chatMessageEntity);
            log.debug("EDIT: Updated chat message with id {}", chatMessageId);

            log.debug("EDIT: Creating edit chat action for chat message with id {}.", chatMessageId);
            var chatActionEntity = new ChatActionEntity();
            chatActionEntity.setChatMessage(chatMessageEntity);
            chatActionEntity.setActionType(ChatActionTypes.EDIT);
            chatActionEntity.setTimestamp(timestamp);
            chatActionEntity.setContent(content);
            chatActionRepository.save(chatActionEntity);
            log.debug("EDIT: Created edit chat action for chat message with id {}.", chatMessageId);
        } else {
            log.debug("EDIT: Content of chat message with it {} has not changed.", chatMessageId);
        }

        return OutboundChatMessage.from(chatMessageRepository.save(chatMessageEntity));
    }

    @Transactional
    public OutboundChatMessage deleteChatMessage(Long chatMessageId) throws NotFoundException {
        log.debug("DELETE: Checking if chat message with id {} exists.", chatMessageId);
        var chatMessageEntity = getChatMessage(chatMessageId);
        log.debug("DELETE: Chat message with id {} exists.", chatMessageId);

        log.debug("DELETE: Checking if chat message with id {} already has been deleted.", chatMessageId);
        if (chatMessageEntity.isDeleted()) {
            log.debug("DELETE: Chat message with id {} has already been deleted.", chatMessageId);
            throw new ChatException("Cannot delete message that has already been deleted.");
        }
        log.debug("DELETE: Chat message with id {} has not been deleted.", chatMessageId);

        log.debug("DELETE: Checking if chat message with id {} has been seen.", chatMessageId);
        if (chatMessageEntity.isSeen()) {
            log.debug("DELETE: Chat message with id {} has been seen.", chatMessageId);
            throw new ChatException("Cannot delete message that has been seen.");
        }
        log.debug("DELETE: Chat message with id {} has not been seen.", chatMessageId);

        var timestamp = LocalDateTime.now();
        log.debug("DELETE: Setting deleted flag for chat message with id {}", chatMessageId);
        chatMessageEntity.setDeleted(true);
        chatMessageEntity.setTimestamp(timestamp);
        chatMessageRepository.save(chatMessageEntity);
        log.debug("DELETE: Updated chat message with id {}", chatMessageId);

        log.debug("DELETE: Creating delete chat action for chat message with id {}.", chatMessageId);
        var chatActionEntity = new ChatActionEntity();
        chatActionEntity.setChatMessage(chatMessageEntity);
        chatActionEntity.setActionType(ChatActionTypes.DELETE);
        chatActionEntity.setTimestamp(timestamp);
        chatActionRepository.save(chatActionEntity);
        log.debug("DELETE: Created delete chat action for chat message with id {}.", chatMessageId);

        return OutboundChatMessage.from(chatMessageRepository.save(chatMessageEntity));
    }

    public ChatMessageEntity getChatMessage(Long id) throws NotFoundException {
        return chatMessageRepository.findById(id).orElseThrow(() -> {
            log.debug("Chat message with id {} not found.", id);
            return new NotFoundException("Chat message not found");
        });
    }
}
