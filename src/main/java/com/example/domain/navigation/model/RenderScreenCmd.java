package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to render a specific screen layout.
 */
public record RenderScreenCmd(
    String screenId,
    String deviceType,
    String accountId, // Optional, for personalization
    Map<String, Object> context
) implements Command {
}