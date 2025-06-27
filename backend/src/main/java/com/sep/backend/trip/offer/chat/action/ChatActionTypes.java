package com.sep.backend.trip.offer.chat.action;

import java.util.Set;

public class ChatActionTypes {

    public final static String SEND = "SEND";

    public final static String SEEN = "SEEN";

    public final static String EDIT = "EDIT";

    public final static String DELETE = "DELETE";


    public final static String[] ALL = {SEND, SEEN, EDIT, DELETE};

    /**
     * Returns whether the provided action type is valid or not.
     *
     * @param actionType The action type.
     * @return Whether the provided action type is valid or not.
     */
    public static boolean isValidActionType(String actionType) {
        return Set.of(ALL).contains(actionType);
    }
}
