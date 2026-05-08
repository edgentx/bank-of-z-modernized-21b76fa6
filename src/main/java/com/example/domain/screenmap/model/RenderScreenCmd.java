package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to generate the presentation layout for a specific screen,
 * adapted for the user's device.
 */
public record RenderScreenCmd(
    String screenMapId,
    String screenId,
    String deviceType,
    Map<String, String> inputFields
) implements Command {
}