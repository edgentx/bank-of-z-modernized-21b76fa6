package com.example.domain.uimodel.repository;

import com.example.domain.uimodel.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}
