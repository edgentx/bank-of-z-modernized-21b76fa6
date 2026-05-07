package com.example.domain.navigation.model;

import com.example.domain.shared.Command;

import java.util.Map;

public record ValidateScreenInputCmd(String screenId, Map<String, String> inputFields) implements Command {}
