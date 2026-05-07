package com.example.domain.screen.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a user device.
 * Part of the user-interface-navigation aggregate.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}