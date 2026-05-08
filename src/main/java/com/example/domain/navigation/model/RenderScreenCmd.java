package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
