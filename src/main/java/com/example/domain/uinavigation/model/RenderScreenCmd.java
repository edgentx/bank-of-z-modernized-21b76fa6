package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device.
 * Context: Legacy 3270 BMS mapping to modern web UI.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}
