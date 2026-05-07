package com.example.domain.s1.model;

import com.example.domain.shared.DomainEvent;

public record S1Event(String aggregateId, String type) implements DomainEvent {}
