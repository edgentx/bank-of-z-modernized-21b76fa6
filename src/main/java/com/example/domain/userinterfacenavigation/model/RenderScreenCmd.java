package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        String deviceType,
        Map<String, String> inputFields
) implements Command {
}
