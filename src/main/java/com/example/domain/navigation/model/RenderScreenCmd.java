package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Set;

/**
 * Command to request the rendering of a specific screen adapted to a device type.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}
