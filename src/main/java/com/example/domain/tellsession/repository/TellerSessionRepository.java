package com.example.domain.tellsession.repository;

import com.example.domain.tellsession.model.TellerSessionAggregate;

import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    TellerSessionAggregate load(String id);
}