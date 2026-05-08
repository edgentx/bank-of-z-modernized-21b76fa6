package com.example.domain.tellerauth.repository;

import com.example.domain.tellerauth.model.TellerSessionAggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    Optional<TellerSessionAggregate> findById(String id);
    void save(TellerSessionAggregate aggregate);
}
