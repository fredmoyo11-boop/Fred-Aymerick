package com.sep.backend.notification;

import java.util.Set;

public class NotificationTypes {

    public static final String TRIP_OFFER_NEW = "TRIP_OFFER_NEW";

    public static final String TRIP_OFFER_REVOKED = "TRIP_OFFER_REVOKED";

    public static final String TRIP_OFFER_ACCEPTED = "TRIP_OFFER_ACCEPTED";

    public static final String TRIP_OFFER_REJECTED = "TRIP_OFFER_REJECTED";

    public static final String[] ALL = {
            TRIP_OFFER_NEW,
            TRIP_OFFER_REVOKED,
            TRIP_OFFER_ACCEPTED,
            TRIP_OFFER_REJECTED
    };

    /**
     * Checks if the given type is a valid notification type.
     *
     * @param type the notification type to check
     * @return true if the type is valid, false otherwise
     */
    public static boolean isValidNotificationType(String type) {
        return Set.of(ALL).contains(type);
    }

}
