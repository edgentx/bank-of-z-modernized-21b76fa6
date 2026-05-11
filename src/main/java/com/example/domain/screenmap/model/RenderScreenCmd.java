package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 * Used by S-21: ScreenMap RenderScreenCmd.
 */
public record RenderScreenCmd(String screenId, String deviceType) implements Command {}
