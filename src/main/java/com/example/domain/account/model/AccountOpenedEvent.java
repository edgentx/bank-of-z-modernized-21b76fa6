package com.example.domain.account.model;

import com.example.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when an account is successfully opened.
 * S-5 Event.
 */
public class AccountOpenedEvent implements DomainEvent {
    private final String aggregateId;
    private final String customerId;
    private final String accountType;
    private final BigDecimal initialDeposit;
    private final String sortCode;
    private final Instant occurredAt;

    public AccountOpenedEvent(String aggregateId, String customerId, String accountType, BigDecimal initialDeposit, String sortCode, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.customerId = customerId;
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
        this.sortCode = sortCode;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "account.opened";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String getCustomerId() { return customerId; }
    public String getAccountType() { return accountType; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public String getSortCode() { return sortCode; }
}
