package com.example.domain.screen.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen map for a target device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
