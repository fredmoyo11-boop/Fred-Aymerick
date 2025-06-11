package com.sep.backend.trip.simulation;

import com.sep.backend.trip.simulation.data.SimulationAction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ActionStore {

    private final Map<Long, List<SimulationAction>> actionsByTrip = new ConcurrentHashMap<>();

    public void addAction(Long tripId, SimulationAction action) {
        actionsByTrip.computeIfAbsent(tripId, k -> new CopyOnWriteArrayList<>()).add(action);
    }

    public List<SimulationAction> getActionsByTrip(Long tripId) {
        return actionsByTrip.getOrDefault(tripId, List.of());
    }

}
