package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;
import java.util.Optional;

public interface TellerSessionRepository {
    void save(TellerSession aggregate);
    Optional<TellerSession> findById(String id);
}
