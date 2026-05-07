package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

import java.util.Objects;

public record ForceBalanceCmd(String batchId, String operatorId, String justification) implements Command {
    public ForceBalanceCmd {
        Objects.requireNonNull(batchId, "batchId cannot be null");
        Objects.requireNonNull(operatorId, "operatorId cannot be null");
        // justification can be empty, but usually required by domain logic
    }
}
