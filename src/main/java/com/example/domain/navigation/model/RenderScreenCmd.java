package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device type.
 * Part of Story S-21: Implement RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType
) implements Command {}
