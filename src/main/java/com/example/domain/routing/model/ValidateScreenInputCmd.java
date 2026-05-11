package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used to enforce BMS (3270) field constraints before routing to backend services.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
