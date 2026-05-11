package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen map layout.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {
}
