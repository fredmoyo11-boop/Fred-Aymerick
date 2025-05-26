package com.sep.backend.trip.request;

public class TripRequestStatus {
    public static final String ACTIVE = "ACTIVE"; //When trip request created

    public static final String DELETED = "DELETED"; //When trip request deleted

    public TripRequestStatus() {
        throw new UnsupportedOperationException("Cannot instantiate RequestStatus.");
    }
}
