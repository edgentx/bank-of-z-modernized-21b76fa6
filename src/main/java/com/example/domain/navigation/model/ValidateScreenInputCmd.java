package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

/**
 * Command to validate user input against a specific ScreenMap definition.
 * Attributes:
 * - screenId: Identifier for the screen map (BMS Mapset).
 * - inputFields: Map of field names to their string values.
 */
public record ValidateScreenInputCmd(
        String screenId,
        Map<String, String> inputFields
) implements Command {
}
