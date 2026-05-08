package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate load(String id);
    void save(TellerSessionAggregate aggregate);
}
