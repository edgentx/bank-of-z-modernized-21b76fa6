package com.example.domain.ui.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout for a given device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
