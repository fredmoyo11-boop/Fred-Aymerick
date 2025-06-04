package com.sep.backend.notification;

import com.sep.backend.ErrorMessages;
import com.sep.backend.NotFoundException;
import com.sep.backend.Roles;
import com.sep.backend.account.AccountService;
import com.sep.backend.entity.NotificationEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final AccountService accountService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    private static final String NOTIFICATION_TOPIC_PREFIX = "/topic/notification/";

    public NotificationService(AccountService accountService, SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository) {
        this.accountService = accountService;
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void sendNotification(Notification notification, String recipientEmail) {

        final String topic = NOTIFICATION_TOPIC_PREFIX + recipientEmail;
        final String notificationType = notification.getNotificationType();

        log.debug("Saving {} notification for {}", notificationType, recipientEmail);
        notification = Notification.from(saveNotification(notification, recipientEmail));
        log.debug("Saved notification for {}", recipientEmail);

        log.debug("Sending {} notification to {} on topic {}", notificationType, recipientEmail, topic);
        messagingTemplate.convertAndSend(topic, notification);
        log.info("Sent {} notification to {} on topic {}", notificationType, recipientEmail, topic);
    }

    private NotificationEntity saveNotification(Notification notification, String recipientEmail) {
        var notificationEntity = new NotificationEntity();
        notificationEntity.setNotificationType(notification.getNotificationType());
        notificationEntity.setMessage(notification.getMessage());

        String role = accountService.getRoleByEmail(recipientEmail);
        switch (role) {
            case Roles.CUSTOMER -> notificationEntity.setCustomer(accountService.getCustomerByEmail(recipientEmail));
            case Roles.DRIVER -> notificationEntity.setDriver(accountService.getDriverByEmail(recipientEmail));
        }

        return notificationRepository.save(notificationEntity);
    }

    private List<NotificationEntity> getCurrentNotificationEntities(Principal principal) {
        String email = principal.getName();
        String role = accountService.getRoleByEmail(email);
        return switch (role) {
            case Roles.CUSTOMER -> notificationRepository.findAllByCustomer_Email(email);
            case Roles.DRIVER -> notificationRepository.findAllByDriver_Email(email);
            default -> throw new NotFoundException(ErrorMessages.NOT_FOUND_USER);
        };
    }

    public List<Notification> getCurrentNotifications(Principal principal) {
        return getCurrentNotificationEntities(principal).stream().map(Notification::from).toList();
    }

}
