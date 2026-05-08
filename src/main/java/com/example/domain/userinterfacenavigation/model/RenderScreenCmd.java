package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen adapted to a device type.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
}