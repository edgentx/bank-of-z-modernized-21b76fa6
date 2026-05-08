package com.example.domain.aggregator.repository;

import com.example.domain.aggregator.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate load(String id);
    TellerSessionAggregate loadOrCreate(String id);
    void save(TellerSessionAggregate aggregate);
}