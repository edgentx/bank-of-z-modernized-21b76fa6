package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen layout.
 */
public record RenderScreenCmd(String screenId, DeviceType deviceType) implements Command {}
