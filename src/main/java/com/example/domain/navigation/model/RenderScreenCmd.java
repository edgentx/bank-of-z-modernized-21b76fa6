package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen map.
 */
public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        ScreenMapAggregate.DeviceType deviceType
) implements Command {
}