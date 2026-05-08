package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record UpdateAccountStatusCmd(String accountNumber, AccountAggregate.Status newStatus) implements Command {
}
