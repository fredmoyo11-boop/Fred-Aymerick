package com.sep.backend.trip.offer.chat.action;

import com.sep.backend.validators.ChatActionTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChatActionTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChatActionType {
    String message() default "Invalid chat action type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}