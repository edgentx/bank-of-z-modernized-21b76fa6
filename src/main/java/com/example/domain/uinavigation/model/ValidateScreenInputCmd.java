package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap definition.
 * Part of User-Interface-Navigation context (S-22).
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
}
