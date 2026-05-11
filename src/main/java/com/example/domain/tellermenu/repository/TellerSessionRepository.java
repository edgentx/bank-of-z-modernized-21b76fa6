package com.example.domain.tellermenu.repository;

import com.example.domain.tellermenu.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    Optional<TellerSessionAggregate> findById(String id);
    void save(TellerSessionAggregate aggregate);
}