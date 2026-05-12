package com.example.api.transaction.dto;

import com.example.domain.transaction.model.PostWithdrawalCmd;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PostWithdrawalRequest(
    @NotBlank String transactionId,
    @NotBlank String accountId,
    @NotNull @Positive BigDecimal amount,
    @NotBlank @Size(min = 3, max = 3) String currency
) {
  public PostWithdrawalCmd toCommand() {
    return new PostWithdrawalCmd(transactionId, accountId, amount, currency);
  }
}
