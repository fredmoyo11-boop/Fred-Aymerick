package com.sep.backend.validators;

import com.sep.backend.notification.NotificationType;
import com.sep.backend.notification.NotificationTypes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotificationTypeValidator implements ConstraintValidator<NotificationType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // if value is null, handled by NotNull annotation
        if (value == null) return true;
        // validate the notification type against the predefined types
        return NotificationTypes.isValidNotificationType(value);
    }

}
