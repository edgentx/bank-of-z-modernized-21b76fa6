package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen adapted for a device type.
 * Context: S-21 user-interface-navigation.
 */
public record RenderScreenCmd(
        String aggregateId,
        String screenId,
        String deviceType,
        Map<String, String> fieldValues
) implements Command {
}