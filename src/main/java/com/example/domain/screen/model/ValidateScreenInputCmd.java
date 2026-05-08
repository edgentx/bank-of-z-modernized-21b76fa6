package com.example.domain.screen.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap definition.
 * Validates presence of mandatory fields and adherence to legacy BMS length constraints.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
}