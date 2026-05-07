package com.example.domain.tellermemory.repository;

import com.example.domain.tellermemory.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSessionAggregate aggregate);
    Optional<TellerSessionAggregate> findById(String id);
}
