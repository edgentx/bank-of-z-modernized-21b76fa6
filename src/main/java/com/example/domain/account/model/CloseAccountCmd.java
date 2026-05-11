package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.math.BigDecimal;

public record CloseAccountCmd(String accountId, String accountNumber) implements Command {
}
