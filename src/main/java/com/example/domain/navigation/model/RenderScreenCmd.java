package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.List;

/**
 * Command to render a specific screen layout adapted for a user's device.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    DeviceType deviceType
) implements Command {}
