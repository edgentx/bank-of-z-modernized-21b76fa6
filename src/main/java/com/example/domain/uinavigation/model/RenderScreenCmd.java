package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType, // e.g., "3270", "WEB", "MOBILE"
    int width,
    int height
) implements Command {}
