package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to trigger the rendering of a specific screen.
 * Part of Story S-21.
 */
public record RenderScreenCmd(
        String screenId,
        String deviceType,
        Map<String, Object> context
) implements Command {}
