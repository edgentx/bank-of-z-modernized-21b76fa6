package com.example.domain.navigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific screen map definition.
 * Used by the front-end (Next.js) or 3270 emulator bridge to ensure data
 * integrity before routing to backend CICS/IMS commands.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
    }
}
