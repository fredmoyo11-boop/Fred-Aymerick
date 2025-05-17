package com.sep.backend.trip.request;

public class TripRequestStatus {
    public static final String ACTIVE = "ACTIVE"; //When triprequest created

    public static final String INPROGRESS = "IN_PROGRESS"; //When triprequest accepted by driver

    public static final String COMPLETED = "COMPLETED"; //When triprequest finished

    public TripRequestStatus() {throw new UnsupportedOperationException("Cannot instantiate RequestStatus.");}
}
