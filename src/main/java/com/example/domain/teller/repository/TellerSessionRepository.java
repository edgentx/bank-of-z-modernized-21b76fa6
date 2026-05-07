package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}
