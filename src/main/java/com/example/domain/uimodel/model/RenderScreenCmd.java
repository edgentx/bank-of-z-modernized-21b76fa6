package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}