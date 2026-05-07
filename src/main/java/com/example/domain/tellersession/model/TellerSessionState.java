package com.example.domain.tellersession.model;

/**
 * Internal state representation for TellerSession.
 * Used to encapsulate the status of the session.
 */
public enum TellerSessionState {
    AUTHENTICATED,
    UNAUTHENTICATED,
    TIMED_OUT,
    NAVIGATION_ERROR
}
