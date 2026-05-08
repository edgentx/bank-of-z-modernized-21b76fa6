package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a given device type.
 * Part of S-21: Implement RenderScreenCmd on ScreenMap.
 */
public record RenderScreenCmd(
    String screenMapId,
    String deviceType,
    String screenId
) implements Command {}
