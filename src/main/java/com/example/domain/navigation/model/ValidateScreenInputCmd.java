package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input for a specific ScreenMap (BMS screen).
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
