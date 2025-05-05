package com.sep.backend.triprequest;

public class TripRequestStatus {
    public static final String ACTIVE = "ACTIVE"; //When triprequest started

    public static final String INPROGRESS = "IN_PROGRESS"; //

    public static final String COMPLETED = "COMPLETED"; //When triprequest finished

    public TripRequestStatus() {throw new UnsupportedOperationException("Cannot instantiate RequestStatus.");}
}
