package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap definition.
 * Enforces BMS length constraints and mandatory field presence.
 */
public record ValidateScreenInputCmd(
    String screenMapId,
    String screenId,
    Map<String, String> inputFields
) implements Command {}
