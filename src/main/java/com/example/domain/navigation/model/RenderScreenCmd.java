package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to request the presentation layout for a specific screen.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    String layoutData // Simplified for this context
) implements Command {}
