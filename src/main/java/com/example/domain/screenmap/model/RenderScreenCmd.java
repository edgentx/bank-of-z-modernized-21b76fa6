package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for the user's device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
