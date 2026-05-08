package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used to enforce UI rules before backend processing.
 */
public record ValidateScreenInputCmd(
        String screenMapId,
        String screenId,
        Map<String, String> inputFields
) implements Command {
}
