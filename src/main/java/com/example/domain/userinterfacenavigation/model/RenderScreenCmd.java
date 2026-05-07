package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen adapted to a device type.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    Map<String, String> mandatoryFields,
    Map<String, String> fieldContent // Used to validate field length constraints
) implements Command {
}