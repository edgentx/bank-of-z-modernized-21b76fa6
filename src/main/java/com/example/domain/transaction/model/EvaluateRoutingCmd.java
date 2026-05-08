package com.example.domain.transaction.model;

import com.example.domain.shared.Command;
import java.util.Map;

public record EvaluateRoutingCmd(String transactionId, String transactionType, Map<String, Object> payload) implements Command {
}