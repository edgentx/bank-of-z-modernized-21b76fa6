package com.example.domain.tellersession.model;

/**
 * Represents the navigation state of the teller terminal.
 * Ensures the context matches the 3270 operational flow.
 */
public enum TellerSessionState {
    ROOT_MENU,
    CUSTOMER_SEARCH,
    ACCOUNT_DETAIL,
    TRANSACTION_ENTRY,
    LOGOUT
}
