package com.example.domain.userinterface.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap configuration.
 */
public record ValidateScreenInputCmd(
        String screenMapId,
        String screenId,
        Map<String, String> inputFields
) implements Command {}
