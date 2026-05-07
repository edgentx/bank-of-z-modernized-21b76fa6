package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen for a user.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType
) implements Command {}
