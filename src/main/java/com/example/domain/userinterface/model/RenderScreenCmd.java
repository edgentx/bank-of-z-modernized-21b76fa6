package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a device.
 * Story S-21: RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    String layoutDefinition // JSON string or similar structure representing the layout
) implements Command {}
