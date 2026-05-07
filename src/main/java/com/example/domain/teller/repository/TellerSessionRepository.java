package com.example.domain.teller.repository;

import com.example.domain.teller.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
    // Standard repository pattern for aggregate roots
}
