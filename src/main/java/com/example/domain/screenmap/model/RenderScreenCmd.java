package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * Part of the user-interface-navigation domain (S-21).
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    String legacyBmsMapSetName
) implements Command {
}
