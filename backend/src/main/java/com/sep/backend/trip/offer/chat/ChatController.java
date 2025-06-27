package com.sep.backend.trip.offer.chat;


import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import com.sep.backend.trip.offer.chat.action.InboundChatAction;
import com.sep.backend.trip.offer.chat.message.OutboundChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages/{tripOfferId}")
    @Operation(description = "Returns all messages of the chat belonging to the trip offer with the specified id.",
            tags = {Tags.CHAT},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Retrieved all chat messages for the trip offer with the specified id.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OutboundChatMessage.class))))})
    public List<OutboundChatMessage> getAllMessages(@PathVariable("tripOfferId") @Parameter(description = "The id of the trip offer.") Long tripOfferId, Principal principal) {
        return chatService.getAllMessages(tripOfferId, principal);
    }

    @GetMapping("/actions/{tripOfferId}")
    @Operation(description = "Returns all actions of the chat belonging to the trip offer with the specified id.",
            tags = {Tags.CHAT},
            responses = {@ApiResponse(responseCode = HttpStatus.OK, description = "Retrieved all chat actions for the trip offer with the specified id.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InboundChatAction.class))))})
    public List<?> getAllActions(@PathVariable("tripOfferId") @Parameter(description = "The id of the trip offer.") Long tripOfferId, Principal principal) {
        return new ArrayList<>();
    }
}
