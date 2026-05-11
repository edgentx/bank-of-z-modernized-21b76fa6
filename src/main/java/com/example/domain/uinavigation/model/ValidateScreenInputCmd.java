package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
