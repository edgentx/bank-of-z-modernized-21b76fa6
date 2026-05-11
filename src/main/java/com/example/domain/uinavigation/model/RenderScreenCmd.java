package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a user.
 * Enforces legacy BMS constraints during the transition period.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType
) implements Command {}
