package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input on a specific screen.
 * Part of Story S-22.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
}
