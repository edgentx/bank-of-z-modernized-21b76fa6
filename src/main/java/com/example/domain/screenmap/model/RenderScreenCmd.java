package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a given device type.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
}
