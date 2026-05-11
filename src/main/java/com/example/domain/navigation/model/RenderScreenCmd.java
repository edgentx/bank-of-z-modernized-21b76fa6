package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device.
 */
public record RenderScreenCmd(String screenMapId, String screenId, String deviceType) implements Command {}
