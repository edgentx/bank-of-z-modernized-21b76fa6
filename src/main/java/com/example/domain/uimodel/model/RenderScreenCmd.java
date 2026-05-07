package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen map for a user device.
 */
public record RenderScreenCmd(String screenId, String layoutName, String deviceType) implements Command {
}
