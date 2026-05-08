package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Enforces legacy BMS constraints and mandatory field presence.
 */
public record ValidateScreenInputCmd(
    String screenMapId,
    String screenId,
    Map<String, String> inputFields
) implements Command {}
