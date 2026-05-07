package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout based on device type.
 * Part of Story S-21.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    String layoutData
) implements Command {
}