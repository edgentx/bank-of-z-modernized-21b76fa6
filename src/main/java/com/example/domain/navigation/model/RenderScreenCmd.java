package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to request the rendering of a specific screen layout.
 */
public record RenderScreenCmd(
    String screenId,
    String layoutId,
    DeviceType deviceType
) implements Command {

    public RenderScreenCmd {
        if (screenId == null || screenId.isBlank()) {
             // Validation logic can be here or in Aggregate. 
             // Pattern in this repo often validates in Aggregate, but primitive checks here are fine.
             // We will enforce strict checks in Aggregate as per "Reject" scenarios.
        }
    }
}
