package com.example.domain.tellermgmt.repository;

import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> load(String id);
}