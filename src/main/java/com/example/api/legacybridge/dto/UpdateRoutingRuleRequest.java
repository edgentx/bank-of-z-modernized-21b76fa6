package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record UpdateRoutingRuleRequest(
    @NotBlank String ruleId,
    @NotBlank String newTarget,
    @NotNull Instant effectiveDate,
    @Positive int rulesVersion
) {
  public UpdateRoutingRuleCmd toCommand() {
    return new UpdateRoutingRuleCmd(ruleId, newTarget, effectiveDate, rulesVersion);
  }
}
