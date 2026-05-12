package com.example.api.account.dto;

import com.example.domain.account.model.UpdateAccountStatusCmd;
import jakarta.validation.constraints.NotBlank;

public record UpdateAccountStatusRequest(
    @NotBlank String newStatus
) {
  public UpdateAccountStatusCmd toCommand(String accountNumber) {
    return new UpdateAccountStatusCmd(accountNumber, newStatus);
  }
}
