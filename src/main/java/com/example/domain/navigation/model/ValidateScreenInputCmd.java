package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used before routing commands to backend systems (CICS/IMS).
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
