package com.example.domain.ui.model;

import com.example.domain.shared.Command;
import java.util.Objects;

/**
 * Command to initiate a teller session.
 * Part of UI/Navigation domain bounded context.
 */
public class StartSessionCmd implements Command {

    private final String sessionId;
    private final String tellerId;
    private final String terminalId;

    public StartSessionCmd(String sessionId, String tellerId, String terminalId) {
        this.sessionId = sessionId;
        this.tellerId = tellerId;
        this.terminalId = terminalId;
    }

    public String sessionId() {
        return sessionId;
    }

    public String tellerId() {
        return tellerId;
    }

    public String terminalId() {
        return terminalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartSessionCmd that = (StartSessionCmd) o;
        return Objects.equals(sessionId, that.sessionId) && Objects.equals(tellerId, that.tellerId) && Objects.equals(terminalId, that.terminalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, tellerId, terminalId);
    }
}
