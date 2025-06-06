package com.sep.backend.notification;

import com.sep.backend.validators.NotificationTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotificationTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationType {

    String message() default "Notification type is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
