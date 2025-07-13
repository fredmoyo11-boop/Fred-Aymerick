package com.sep.backend.trip.simulation;

import com.sep.backend.trip.simulation.data.SimulationAction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ActionStore {

    private final Map<Long, List<SimulationAction>> actionsByTrip = new ConcurrentHashMap<>();

    /**
     * Stores a simulation action for the specified trip offer id.
     *
     * @param tripOfferId The trip offer id.
     * @param action      The action to be stored.
     */
    public void addAction(Long tripOfferId, SimulationAction action) {
        actionsByTrip.computeIfAbsent(tripOfferId, k -> new CopyOnWriteArrayList<>()).add(action);
    }

    /**
     * Returns all stored simulation actions for specified trip offer id.
     *
     * @param tripOfferId The trip offer id.
     * @return The list of the stored simulation actions.
     */
    public List<SimulationAction> getActionsByTrip(Long tripOfferId) {
        return actionsByTrip.getOrDefault(tripOfferId, List.of());
    }

    /**
     * Returns an optional containing the last simulation action if action exist, else empty optional
     *
     * @param tripOfferId The id of the trip offer
     * @return The optional containing the last simulation action if exists, else empty optional
     */
    public Optional<SimulationAction> getLastSimulationAction(Long tripOfferId) {
        List<SimulationAction> simulationActions = actionsByTrip.get(tripOfferId);
        if (simulationActions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(simulationActions.getLast());
    }

    /**
     * Returns the last animation index for a simulation.
     *
     * @param tripOfferId The id of the trip offer.
     * @return The animation index.
     */
    public Integer getLastAnimationIndex(Long tripOfferId) {
        Optional<SimulationAction> action = getLastSimulationAction(tripOfferId);
        if (action.isPresent()) {
            return action.get().getParameters().getStartIndex();
        } else {
            return 0;
        }
    }
}
