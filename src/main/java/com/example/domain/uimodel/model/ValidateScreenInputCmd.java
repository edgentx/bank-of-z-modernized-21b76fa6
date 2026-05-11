package com.example.domain.uimodel.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific screen map.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
