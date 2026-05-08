package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}