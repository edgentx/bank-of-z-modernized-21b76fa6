package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap.
 * Used to enforce legacy BMS constraints and mandatory fields.
 */
public record ValidateScreenInputCmd(
        String screenId,
        Map<String, String> inputFields
) implements Command {}
