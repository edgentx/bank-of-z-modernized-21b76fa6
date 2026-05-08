package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;
import java.util.UUID;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(UUID id);
}
