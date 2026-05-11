package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    Map<String, Object> context
) implements Command {
    public RenderScreenCmd {
        if (screenMapId == null) throw new IllegalArgumentException("screenMapId required");
    }
}