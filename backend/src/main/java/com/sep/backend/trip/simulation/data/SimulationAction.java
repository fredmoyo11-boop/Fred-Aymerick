package com.sep.backend.trip.simulation.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Represents an inbound simulation action.")
public class SimulationAction {

    @Schema(description = "The type of the action.", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
            SimulationActionTypes.START,
            SimulationActionTypes.STOP,
            SimulationActionTypes.CHANGE_VELOCITY,
            SimulationActionTypes.COMPLETE,
            SimulationActionTypes.INFO,
            SimulationActionTypes.LOCK,
            SimulationActionTypes.UNLOCK,})
    private String actionType;

    @Schema(description = "The timestamp of the action.", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime timestamp;

    @Schema(description = "The parameters of the action.", requiredMode = Schema.RequiredMode.REQUIRED)
    private SimulationActionParameters parameters;
}
