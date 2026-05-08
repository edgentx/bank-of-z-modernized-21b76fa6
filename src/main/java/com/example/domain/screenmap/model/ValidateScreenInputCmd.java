package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used in the 3270/TN3270 web terminal emulator flow.
 */
public record ValidateScreenInputCmd(
    String screenMapId,
    Map<String, String> inputFields
) implements Command {}