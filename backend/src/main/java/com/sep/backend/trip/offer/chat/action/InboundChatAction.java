package com.sep.backend.trip.offer.chat.action;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents a chat action.")
public class InboundChatAction {

    @ChatActionType
    @Schema(description = "The type of the message.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String actionType;

    @Schema(description = "The content of the message. Might be null if action type is DELETE.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String content;

    @Schema(description = "The id of the chat message. Might be null if action type is SEND.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long chatMessageId;
}
