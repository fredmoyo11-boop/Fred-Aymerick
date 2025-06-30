package com.sep.backend.trip.simulation.data;

public class SimulationActionTypes {

    /**
     * Starts the simulation.
     */
    public static final String START = "START";

    /**
     * Stops the simulation.
     */
    public static final String STOP = "STOP";

    /**
     * Reroutes the simulation.
     */
    public static final String REROUTE = "REROUTE";

    /**
     * Changes the velocity of the simulation.
     */
    public static final String CHANGE_VELOCITY = "CHANGE_VELOCITY";

    /**
     * Completes the simulation.
     */
    public static final String COMPLETE = "COMPLETE";


    /**
     * Locks the simulation.
     */
    public static final String LOCK = "LOCK";

    /**
     * Unlocks the simulation.
     */
    public static final String UNLOCK = "UNLOCK";

    /**
     * Marks presence of the driver.
     */
    public static final String DRIVER_PRESENT = "DRIVER_PRESENT";

    /**
     * Acknowledges the presence of the driver.
     */
    public static final String ACK_DRIVER_PRESENT = "ACK_DRIVER_PRESENT";

    /**
     * Marks presence of the customer.
     */
    public static final String CUSTOMER_PRESENT = "CUSTOMER_PRESENT";

    /**
     * Acknowledges the presence of the customer.
     */
    public static final String ACK_CUSTOMER_PRESENT = "ACK_CUSTOMER_PRESENT";


    // request the current state of the information, might be useful after disconnect -> reconnect
    /**
     * Returns the current information of the simulation.
     */
    public static final String INFO = "INFO";

    public static final String[] ALL = {START, STOP, CHANGE_VELOCITY, COMPLETE, LOCK, UNLOCK, INFO, DRIVER_PRESENT, ACK_DRIVER_PRESENT, CUSTOMER_PRESENT, ACK_CUSTOMER_PRESENT};

}
