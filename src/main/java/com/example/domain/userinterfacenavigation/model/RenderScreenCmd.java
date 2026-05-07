package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device.
 * Part of S-21: RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(
    String aggregateId,
    String screenId,
    String deviceType,
    String rawLayoutData
) implements Command {}
