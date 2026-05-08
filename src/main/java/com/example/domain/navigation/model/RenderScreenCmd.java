package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * Story: S-21
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    String layoutTemplate,
    String metadataJson
) implements Command {}
