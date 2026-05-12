package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.EvaluateRoutingCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.Map;

public record EvaluateRoutingRequest(
    @NotBlank String routeId,
    @NotBlank String transactionType,
    Map<String, Object> payload,
    @Positive int rulesVersion
) {
  public EvaluateRoutingCmd toCommand() {
    return new EvaluateRoutingCmd(routeId, transactionType, payload, rulesVersion);
  }
}
