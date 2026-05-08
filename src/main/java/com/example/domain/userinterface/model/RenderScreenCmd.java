package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a user's device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}
