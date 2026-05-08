package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

public interface TellerSessionRepository {
    TellerSession load(String id);
    void save(TellerSession aggregate);
}
