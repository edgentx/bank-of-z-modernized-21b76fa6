package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record OpenAccountCmd(String accountId, String accountNumber, String ownerName) implements Command {}
