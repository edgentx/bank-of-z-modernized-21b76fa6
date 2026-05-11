package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used in the 3270 terminal emulator bridge before routing to backend CICS/IMS transactions.
 */
public record ValidateScreenInputCmd(String screenMapId, Map<String, String> inputFields) implements Command {
}