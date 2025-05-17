package com.sep.backend.account;

public class CarType {
    public static final String SMALL = "SMALL";

    public static final String MEDIUM = "MEDIUM";

    public static final String DELUXE = "DELUXE";

    public static boolean isValidCarType(String carType) {
        return carType.equals(SMALL) || carType.equals(MEDIUM) || carType.equals(DELUXE);
    }

    public CarType() {throw new UnsupportedOperationException("Cannot instantiate CarType.");}
}