package com.example.domain.teller.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}