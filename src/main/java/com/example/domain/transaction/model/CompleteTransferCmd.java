package com.example.domain.transaction.model;

import com.example.domain.shared.Command;

public record CompleteTransferCmd(String transferReference) implements Command {}
