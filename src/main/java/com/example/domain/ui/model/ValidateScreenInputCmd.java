package com.example.domain.ui.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ValidateScreenInputCmd(
    String screenMapId,
    String screenId,
    Map<String, String> inputFields
) implements Command {}
