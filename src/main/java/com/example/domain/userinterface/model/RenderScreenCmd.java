package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;
import java.util.List;
import java.util.Map;

/**
 * Command to render a specific screen for a user.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    List<Map<String, String>> inputs
) implements Command {}
