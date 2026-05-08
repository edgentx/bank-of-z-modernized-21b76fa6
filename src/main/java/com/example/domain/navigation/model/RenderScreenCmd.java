package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * Part of the user-interface-navigation aggregate (ScreenMap).
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
}
