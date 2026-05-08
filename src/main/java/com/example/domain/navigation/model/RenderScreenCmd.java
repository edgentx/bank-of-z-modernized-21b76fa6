package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout adapted for a device.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    Map<String, String> context
) implements Command {}
