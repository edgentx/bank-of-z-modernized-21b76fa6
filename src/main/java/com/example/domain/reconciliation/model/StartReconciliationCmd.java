package com.example.domain.reconciliation.model;

import com.example.domain.shared.Command;

public record StartReconciliationCmd(String batchId, String startDate, String endDate) implements Command {
}