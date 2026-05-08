package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;

import java.util.Map;

/**
 * Command to validate user input against a specific Screen Map definition.
 * Used to enforce front-end integrity before routing to CICS/IMS backends.
 */
public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
