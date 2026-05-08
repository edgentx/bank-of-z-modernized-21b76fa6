package com.example.domain.account.model;

import com.example.domain.shared.Command;
import com.example.domain.account.model.AccountAggregate.AccountStatus;

public record UpdateAccountStatusCmd(String accountNumber, AccountStatus newStatus) implements Command {}
