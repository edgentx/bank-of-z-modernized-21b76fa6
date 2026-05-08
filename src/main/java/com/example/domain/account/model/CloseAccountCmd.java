package com.example.domain.account.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record CloseAccountCmd(String accountNumber) implements Command {
    public CloseAccountCmd {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber cannot be null or blank");
        }
    }
}