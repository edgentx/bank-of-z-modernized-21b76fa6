package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen adapted for a device type.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}