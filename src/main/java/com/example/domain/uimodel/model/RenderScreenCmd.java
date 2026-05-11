package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device type.
 * Part of S-21: RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType
) implements Command {}
