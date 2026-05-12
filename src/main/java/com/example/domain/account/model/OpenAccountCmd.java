package com.example.domain.account.model;
import com.example.domain.shared.Command;
public record OpenAccountCmd(String accountId, String customerId, String accountType, long initialDeposit, String sortCode) implements Command {}
