package com.example.domain.screen.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout.
 * Part of Story S-21.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType
) implements Command {}
