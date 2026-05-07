package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen layout
 * adapted for a specific device type.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
