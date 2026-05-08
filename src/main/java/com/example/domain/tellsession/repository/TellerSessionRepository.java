package com.example.domain.tellsession.repository;

import com.example.domain.tellsession.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate load(String id);
    void save(TellerSessionAggregate aggregate);
}
