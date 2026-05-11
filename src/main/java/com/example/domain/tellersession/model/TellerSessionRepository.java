package com.example.domain.tellersession.model;

import com.example.domain.shared.Aggregate;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
