package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen map layout.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {}
