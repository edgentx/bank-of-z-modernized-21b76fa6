package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen adapted for a device.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType, Map<String, String> fieldData) implements Command {
    public RenderScreenCmd {
        if (screenMapId == null || screenMapId.isBlank()) throw new IllegalArgumentException("screenMapId required");
    }
}
