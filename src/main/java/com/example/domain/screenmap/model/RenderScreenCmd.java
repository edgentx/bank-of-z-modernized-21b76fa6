package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * This is part of the user-interface-navigation feature (S-21).
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    int screenHeight,
    int screenWidth
) implements Command {}
