package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

/**
 * Command to validate user input for a specific screen.
 * Bounded Context: user-interface-navigation
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
