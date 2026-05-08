package com.example.domain.tellersession.model;

public class TellerSession {
    public enum State {
        UNAUTHENTICATED,
        AUTHENTICATED,
        TIMED_OUT,
        LOCKED
    }
}
