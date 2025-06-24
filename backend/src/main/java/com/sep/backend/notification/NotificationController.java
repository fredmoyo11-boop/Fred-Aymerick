package com.sep.backend.notification;

import com.sep.backend.HttpStatus;
import com.sep.backend.Tags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/notification", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @GetMapping("/current")
    @Operation(description = "Returns all notifications for the current user.",
            tags = {Tags.NOTIFICATION},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Notifications retrieved successfully.",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Notification.class))))})
    public List<Notification> getCurrentNotifications(Principal principal) {
        return notificationService.getCurrentNotifications(principal);
    }

}

