package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(String screenId, String deviceType, int width, int height) implements Command {
}
