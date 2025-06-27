package com.sep.backend.validators;

import com.sep.backend.trip.offer.chat.action.ChatActionType;
import com.sep.backend.trip.offer.chat.action.ChatActionTypes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChatActionTypeValidator implements ConstraintValidator<ChatActionType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ChatActionTypes.isValidActionType(value);
    }
}
