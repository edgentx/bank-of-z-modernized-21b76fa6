package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a user device.
 */
public record RenderScreenCmd(String screenId, String deviceType, String definition) implements Command {}
