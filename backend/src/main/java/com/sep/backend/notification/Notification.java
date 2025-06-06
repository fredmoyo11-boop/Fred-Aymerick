package com.sep.backend.notification;

import com.sep.backend.entity.NotificationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Schema(description = "Represents a notification.")
public class Notification {

    /**
     * The id of the notification.
     */
    @NotNull
    @Schema(description = "The unique id of the notification.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * The type of the notification.
     */
    @NotNull
    @NotificationType
    @Schema(description = "The type of the notification.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String notificationType;

    /**
     * The message of the notification.
     */
    @NotBlank
    @Schema(description = "The message of the notification.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    public static Notification from(NotificationEntity entity) {
        var notification = new Notification();
        notification.setId(entity.getId());
        notification.setNotificationType(entity.getNotificationType());
        notification.setMessage(entity.getMessage());
        return notification;
    }

}
