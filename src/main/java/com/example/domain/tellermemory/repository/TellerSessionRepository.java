package com.example.domain.tellermemory.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
