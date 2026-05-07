package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout adapted for a device.
 * Part of S-21: ScreenMap Aggregate implementation.
 */
public record RenderScreenCmd(String screenId, String deviceType, Map<String, String> inputData) implements Command {
}
