package com.example.domain.routing.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific ScreenMap (BMS map definition).
 * Used by the UI Navigation layer to ensure data integrity before routing to backend transactions.
 */
public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields
) implements Command {}
