package com.example.domain.screening.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific Screen Map definition.
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
