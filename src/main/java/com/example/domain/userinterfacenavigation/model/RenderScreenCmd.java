package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen layout adapted for a device type.
 * Validation includes mandatory fields and legacy BMS field length constraints.
 */
public record RenderScreenCmd(
        String screenMapId,
        String screenId,
        String deviceType,
        Map<String, String> fieldValues
) implements Command {}
