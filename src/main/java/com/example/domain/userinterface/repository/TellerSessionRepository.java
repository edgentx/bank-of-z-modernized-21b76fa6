package com.example.domain.userinterface.repository;

import com.example.domain.userinterface.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
}
