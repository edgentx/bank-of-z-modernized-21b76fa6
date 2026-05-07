package com.example.domain;

import com.example.domain.shared.DomainEvent;

public record S1Event(String aggregateId, String type, String payload) implements DomainEvent {
    @Override
    public String type() {
        return type;
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
