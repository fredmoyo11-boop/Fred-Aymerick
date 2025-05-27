package com.sep.backend;

public abstract class CarTypes {

    public static final String SMALL = "SMALL";
    public static final String MEDIUM = "MEDIUM";
    public static final String DELUXE = "DELUXE";

    /**
     * Returns the price per kilometer in euro for the specified car type.
     *
     * @param carType The car type.
     * @return The price per kilometer in euro.
     * @throws IllegalArgumentException If an invalid car type is provided.
     */
    public static double getPricePerKilometer(String carType) throws IllegalArgumentException {
        return switch (carType) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case DELUXE -> 10;
            default -> throw new IllegalArgumentException("Invalid car type.");
        };
    }

    /**
     * Returns whether the specified car type is valid or not.
     *
     * @param carType The car type to check.
     * @return Whether the specified car type is valid or not.
     */
    public static boolean isValidCarType(String carType) {
        return carType.equals(SMALL) || carType.equals(MEDIUM) || carType.equals(DELUXE);
    }

}
