package com.example.domain.screen.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen for a given device type.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    Map<String, Object> inputData
) implements Command {}
