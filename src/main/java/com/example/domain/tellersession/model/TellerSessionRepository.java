package com.example.domain.tellersession.model;

import com.example.domain.shared.Aggregate;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
    TellerSessionAggregate create(String id);
}
