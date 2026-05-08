package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

import.util.Optional;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
