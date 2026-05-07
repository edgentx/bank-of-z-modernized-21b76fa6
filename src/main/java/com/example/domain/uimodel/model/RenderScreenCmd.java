package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen layout.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {}
