package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a ScreenMap definition.
 * S-22: user-interface-navigation.
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
