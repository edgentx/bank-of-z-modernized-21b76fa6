package com.example.domain.tellersession.repository;

import com.example.domain.tellersession.model.TellerSession;

public interface TellerSessionRepository {
    TellerSession save(TellerSession aggregate);
    TellerSession load(String id);
}