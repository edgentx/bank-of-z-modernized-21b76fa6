package com.example.domain.account.model;

import com.example.domain.shared.Command;

public record CloseAccountCmd(String accountNumber, String accountNumberCheck) implements Command {
}