package com.example.domain.tellsession.repository;

import com.example.domain.tellsession.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}
