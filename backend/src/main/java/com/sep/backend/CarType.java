package com.sep.backend;

import jakarta.validation.constraints.NotBlank;

public class CarType {
    public static final String MEDIUM = "MEDIUM";

    public static final String LARGE = "LARGE";

    public static final String DELUXE = "DELUXE";

    public CarType() {throw new UnsupportedOperationException("Cannot instantiate CarType.");}

    public static boolean isValidCarType(@NotBlank String carType) {
        return carType.equals(MEDIUM) || carType.equals(LARGE) || carType.equals(DELUXE);
    }
}