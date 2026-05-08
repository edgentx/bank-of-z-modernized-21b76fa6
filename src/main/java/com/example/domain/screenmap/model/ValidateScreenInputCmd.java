package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Enforces BMS field length constraints and mandatory field presence.
 */
public record ValidateScreenInputCmd(
        String screenId,
        Map<String, String> inputFields
) implements Command {}
