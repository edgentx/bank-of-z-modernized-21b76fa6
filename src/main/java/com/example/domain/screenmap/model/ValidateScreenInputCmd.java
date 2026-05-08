package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against screen map rules.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {
}
