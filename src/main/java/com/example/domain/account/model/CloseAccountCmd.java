package com.example.domain.account.model;

import com.example.domain.shared.Command;

/**
 * Command to close an account.
 * S-7: Closes the account permanently, provided the balance is zero.
 */
public record CloseAccountCmd(String accountNumber) implements Command {}
