package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a device type.
 * Context: S-21 RenderScreenCmd.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    String layoutDefinition
) implements Command {}
