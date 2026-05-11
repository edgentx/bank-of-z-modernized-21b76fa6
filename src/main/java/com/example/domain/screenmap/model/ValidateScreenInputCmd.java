package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against the screen map rules.
 * Used by the 3270/TN3270 web terminal emulator to ensure data integrity before routing to backend CICS/IMS transactions.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {

    public ValidateScreenInputCmd {
        if (screenId == null || screenId.isBlank()) {
            throw new IllegalArgumentException("screenId cannot be null or blank");
        }
        // InputFields can be empty (e.g. function keys), but must not be null.
        if (inputFields == null) {
            throw new IllegalArgumentException("inputFields cannot be null");
        }
    }
}
