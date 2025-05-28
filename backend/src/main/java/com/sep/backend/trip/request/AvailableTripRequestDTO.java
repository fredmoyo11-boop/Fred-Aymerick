package com.sep.backend.trip.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableTripRequestDTO {
    private Long requestId;
    private LocalDateTime requestTime;
    private String customerUsername;
    private double customerRating;
    private String carType;
    private double distanceInKm;
}

