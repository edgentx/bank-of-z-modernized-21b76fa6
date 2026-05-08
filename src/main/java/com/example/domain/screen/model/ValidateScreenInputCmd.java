package com.example.domain.screen.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input for a specific screen.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
