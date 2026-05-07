package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to generate the presentation layout for a specific screen,
 * adapted for the user's device.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    Map<String, String> fieldValues
) implements Command {}
