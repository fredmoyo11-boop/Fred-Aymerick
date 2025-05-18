package com.sep.backend;

import java.util.Set;

public class CarType {
    public static final String SMALL = "SMALL";

    public static final String MEDIUM = "MEDIUM";

    public static final String DELUXE = "DELUXE";

    public static final String[] ALL = {SMALL, MEDIUM, DELUXE};

    public static boolean isValidCarType(String carType) {
        return Set.of(ALL).contains(carType);
    }

    public CarType() {
        throw new UnsupportedOperationException("Cannot instantiate CarType.");
    }
}