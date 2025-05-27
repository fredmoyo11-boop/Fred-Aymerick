package com.sep.backend.trip.request;

public class DistanceNotFoundException extends RuntimeException {
    public DistanceNotFoundException(String message) {
        super(message);
    }
}
