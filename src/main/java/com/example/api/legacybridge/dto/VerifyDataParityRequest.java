package com.example.api.legacybridge.dto;

import com.example.domain.legacybridge.model.VerifyDataParityCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record VerifyDataParityRequest(
    @NotBlank String checkpointId,
    @NotBlank String entityType,
    @PositiveOrZero long syncOffset,
    @NotBlank String dateRange,
    @NotBlank String validationHash
) {
  public VerifyDataParityCmd toCommand() {
    return new VerifyDataParityCmd(checkpointId, entityType, syncOffset, dateRange, validationHash);
  }
}
