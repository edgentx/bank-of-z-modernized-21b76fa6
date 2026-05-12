package com.example.api.tellersession.dto;

import com.example.domain.tellersession.model.StartSessionCmd;
import jakarta.validation.constraints.NotBlank;

public record StartSessionRequest(
    @NotBlank String tellerId,
    @NotBlank String terminalId
) {
  public StartSessionCmd toCommand() {
    return new StartSessionCmd(tellerId, terminalId);
  }
}
