package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used to enforce BMS field constraints and mandatory field presence
 * before routing to backend commands.
 */
public record ValidateScreenInputCmd(
        String screenMapId,
        String screenId,
        Map<String, String> inputFields
) implements Command {}
