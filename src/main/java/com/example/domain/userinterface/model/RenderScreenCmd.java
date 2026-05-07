package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {
}
