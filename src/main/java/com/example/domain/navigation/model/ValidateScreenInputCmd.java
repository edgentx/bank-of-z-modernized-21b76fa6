package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

/**
 * Command to validate user input against a specific screen map definition.
 * Ensures mandatory fields are present and field lengths adhere to legacy constraints.
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields,
    Set<String> requiredFields,
    Map<String, Integer> fieldLengthConstraints // fieldName -> maxLength
) implements Command {
}