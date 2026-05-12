package com.example.api.transaction.dto;

import com.example.domain.transaction.model.ReverseTransactionCmd;
import jakarta.validation.constraints.NotBlank;

public record ReverseTransactionRequest(
    @NotBlank String reason
) {
  public ReverseTransactionCmd toCommand(String transactionId) {
    return new ReverseTransactionCmd(transactionId, reason);
  }
}
