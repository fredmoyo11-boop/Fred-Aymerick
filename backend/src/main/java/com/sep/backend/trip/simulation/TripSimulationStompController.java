package com.sep.backend.trip.simulation;

import com.sep.backend.trip.simulation.data.SimulationAction;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TripSimulationStompController {
    private final TripSimulationService tripSimulationService;

    public TripSimulationStompController(TripSimulationService tripSimulationService) {
        this.tripSimulationService = tripSimulationService;
    }

    @MessageMapping("/simulation/{tripOfferId}")
    @SendTo("/topic/simulation/{tripOfferId}")
    public SimulationAction sendSimulationAction(@DestinationVariable Long tripOfferId, @Payload SimulationAction simulationAction, Principal principal) {
        return tripSimulationService.sendSimulationAction(tripOfferId, simulationAction, principal);
    }
}
