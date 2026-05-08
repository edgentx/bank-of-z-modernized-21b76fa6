package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a user device.
 * Part of S-21: ScreenMap Rendering.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
}