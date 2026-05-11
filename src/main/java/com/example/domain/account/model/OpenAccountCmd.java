package com.example.domain.account.model;

import com.example.domain.shared.Command;
import java.math.BigDecimal;

public record OpenAccountCmd(String customerId, String accountType, BigDecimal initialDeposit, String sortCode) implements Command {}