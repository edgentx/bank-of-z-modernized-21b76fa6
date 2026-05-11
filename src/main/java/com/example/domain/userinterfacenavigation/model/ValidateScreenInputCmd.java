package com.example.domain.userinterfacenavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;
import java.util.Set;

public record ValidateScreenInputCmd(
    String screenId,
    Map<String, String> inputFields,
    Set<String> mandatoryFields,
    Map<String, Integer> fieldLengths
) implements Command {}
