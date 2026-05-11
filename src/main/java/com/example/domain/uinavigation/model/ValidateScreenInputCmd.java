package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input for a specific screen map.
 * Part of the user-interface-navigation context.
 */
public record ValidateScreenInputCmd(
        String screenMapId,
        String screenId,
        Map<String, String> inputFields
) implements Command {
}