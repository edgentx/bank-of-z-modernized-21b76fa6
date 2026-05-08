package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

/**
 * Command to trigger the rendering of a specific screen.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    String layoutData
) implements Command {}
