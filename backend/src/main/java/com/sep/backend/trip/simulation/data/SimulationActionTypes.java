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

    // request the current state of the information, might be useful after disconnect -> reconnect
    /**
     * Returns the current information of the simulation.
     */
    public static final String INFO = "INFO";

    public static final String[] ALL = {START, STOP, CHANGE_VELOCITY, COMPLETE, LOCK, UNLOCK, INFO};

}
