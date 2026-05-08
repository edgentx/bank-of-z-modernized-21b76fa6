package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a specific device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
