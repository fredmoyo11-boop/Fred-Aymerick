package com.sep.backend.trip.offer.chat.message;

import com.sep.backend.entity.ChatMessageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Represents an outbound chat message.")
public class OutboundChatMessage {

    @Schema(description = "The id of the trip offer.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tripOfferId;

    @Schema(description = "The id of the chat message.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long chatMessageId;

    @Schema(description = "The direction of the message. Either C2D or D2C.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direction;

    @Schema(description = "The creation timestamp of the message.", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdTimestamp;

    @Schema(description = "The latest timestamp of the message.", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime timestamp;

    @Schema(description = "The content of the message.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "Whether the message has been seen by the recipient.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean seen;

    @Schema(description = "Whether the message has been edited by the sender.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean edited;

    @Schema(description = "Whether the message has been deleted by the sender.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean deleted;


    public static OutboundChatMessage from(ChatMessageEntity entity) {
        var outboundChatMessage = new OutboundChatMessage();
        outboundChatMessage.setTripOfferId(entity.getTripOffer().getId());
        outboundChatMessage.setChatMessageId(entity.getId());
        outboundChatMessage.setDirection(entity.getDirection());
        outboundChatMessage.setCreatedTimestamp(entity.getCreatedTimestamp());
        outboundChatMessage.setTimestamp(entity.getTimestamp());
        outboundChatMessage.setContent(entity.getContent());
        outboundChatMessage.setSeen(entity.isSeen());
        outboundChatMessage.setEdited(entity.isEdited());
        outboundChatMessage.setDeleted(entity.isDeleted());
        return outboundChatMessage;
    }
}
