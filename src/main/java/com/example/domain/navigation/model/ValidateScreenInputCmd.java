package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Part of the user-interface-navigation context (S-22).
 */
public record ValidateScreenInputCmd(String screenMapId, Map<String, String> inputFields) implements Command {}