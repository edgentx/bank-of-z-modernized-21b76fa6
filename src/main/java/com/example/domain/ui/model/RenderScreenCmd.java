package com.example.domain.ui.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a target device.
 */
public record RenderScreenCmd(String screenId, DeviceType deviceType) implements Command {}
