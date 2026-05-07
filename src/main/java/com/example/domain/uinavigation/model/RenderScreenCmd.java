package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen adapted for a user's device.
 * Part of User Interface Navigation (ScreenMap) domain.
 */
public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType
) implements Command {}
