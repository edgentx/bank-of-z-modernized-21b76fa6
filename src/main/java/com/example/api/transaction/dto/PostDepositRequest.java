package com.example.api.transaction.dto;

import com.example.domain.transaction.model.PostDepositCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PostDepositRequest(
    @NotBlank String transactionId,
    @NotBlank String accountId,
    @NotNull @Positive BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currency
) {
  public PostDepositCmd toCommand() {
    return new PostDepositCmd(transactionId, accountId, amount, currency);
  }
}
