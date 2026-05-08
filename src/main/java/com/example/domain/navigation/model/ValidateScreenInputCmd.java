package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap definition.
 * Used to enforce BMS constraints and business rules before routing to backend commands.
 */
public record ValidateScreenInputCmd(String screenMapId, Map<String, String> inputFields) implements Command {}
