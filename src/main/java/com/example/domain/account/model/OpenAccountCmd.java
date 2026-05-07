package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record OpenAccountCmd(String accountId, String customerId, String accountType, String currency) implements Command {}
