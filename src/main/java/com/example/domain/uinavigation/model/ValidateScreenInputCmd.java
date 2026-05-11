package com.example.domain.uinavigation.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record ValidateScreenInputCmd(String screenMapId, Map<String, String> inputFields) implements Command {
}
