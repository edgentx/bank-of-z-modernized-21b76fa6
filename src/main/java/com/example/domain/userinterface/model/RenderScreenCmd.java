package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * Part of Story S-21: ScreenMap aggregate logic.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType
) implements Command {
}