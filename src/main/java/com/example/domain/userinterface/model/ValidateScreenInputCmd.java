package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used by the UI layer to enforce 3270/BMS constraints before routing to CICS/IMS backends.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
}
