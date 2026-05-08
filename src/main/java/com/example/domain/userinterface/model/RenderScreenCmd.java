package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen based on device type.
 * Validates legacy BMS constraints.
 */
public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        String deviceType,
        Map<String, String> fields
) implements Command {
}
