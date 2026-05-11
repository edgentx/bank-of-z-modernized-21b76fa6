package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record UpdateAccountStatusCmd(
  String accountId,
  String accountNumber,
  String newStatus,
  BigDecimal currentBalance,
  String accountType
) implements Command {}
