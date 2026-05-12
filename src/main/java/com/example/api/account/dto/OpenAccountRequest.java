package com.example.api.account.dto;

import com.example.domain.account.model.OpenAccountCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record OpenAccountRequest(
    @NotBlank String accountId,
    @NotBlank String customerId,
    @NotBlank String accountType,
    @PositiveOrZero long initialDeposit,
    @NotBlank String sortCode
) {
  public OpenAccountCmd toCommand() {
    return new OpenAccountCmd(accountId, customerId, accountType, initialDeposit, sortCode);
  }
}
