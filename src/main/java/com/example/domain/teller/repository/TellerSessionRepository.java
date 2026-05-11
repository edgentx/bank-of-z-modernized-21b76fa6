package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate load(String sessionId);
    void save(TellerSessionAggregate aggregate);
}
