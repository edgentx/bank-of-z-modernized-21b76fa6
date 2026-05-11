package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to request the rendering of a specific screen layout.
 */
public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        String deviceType
) implements Command {
}