package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSessionAggregate;

public interface TellerSessionRepository {
    TellerSessionAggregate save(TellerSessionAggregate aggregate);
    TellerSessionAggregate findById(String id);
}