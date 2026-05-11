package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

// Fixed Repository Interface
// The previous build failed because it referenced a non-existent 'TellerSession' class.
// We reference the correct Aggregate class: TellerSessionAggregate.
public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}
