package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to request the rendering of a specific screen layout
 * adapted for the user's device.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType
) implements Command {}
