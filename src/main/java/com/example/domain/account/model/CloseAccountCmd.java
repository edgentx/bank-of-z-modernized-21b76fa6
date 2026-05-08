package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to close an account.
 */
public record CloseAccountCmd(String accountNumber) implements Command {}
