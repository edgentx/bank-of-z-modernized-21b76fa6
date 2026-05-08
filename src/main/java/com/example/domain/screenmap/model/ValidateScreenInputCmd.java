package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Ensures data integrity before routing to backend CICS/IMS transactions.
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {
}
