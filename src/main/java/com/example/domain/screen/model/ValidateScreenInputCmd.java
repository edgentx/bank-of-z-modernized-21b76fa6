package com.example.domain.screen.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Context: BANK S-22 - User Interface Navigation.
 */
public record ValidateScreenInputCmd(
    String aggregateId,
    String screenId,
    Map<String, String> inputFields
) implements Command {}
