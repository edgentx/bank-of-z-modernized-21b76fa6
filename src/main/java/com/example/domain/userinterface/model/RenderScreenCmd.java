package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device type.
 * Story S-21
 */
public record RenderScreenCmd(
        String aggregateId,
        String screenId,
        String deviceType
) implements Command {
}
