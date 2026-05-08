package com.example.domain.screenmap.model;

import com.example.domain.shared.Command;
import java.util.Map;

/**
 * Command to validate user input against a specific Screen Map definition.
 */
public record ValidateScreenInputCmd(String screenMapId, Map<String, String> inputFields) implements Command {}
