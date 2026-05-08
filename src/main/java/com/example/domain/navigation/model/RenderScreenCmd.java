package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to render a specific screen layout adapted for a user's device.
 * Story S-21.
 */
public record RenderScreenCmd(
    String screenId,
    DeviceType deviceType,
    String layoutId,
    Map<String, String> inputFields
) implements Command {}
