package com.example.domain.tellersession.model;

import java.util.Objects;

public class TellerSessionState {
    private final Context context;
    private final Status status;

    public TellerSessionState() {
        this(Context.UNKNOWN, Status.INACTIVE);
    }

    public TellerSessionState(Context context, Status status) {
        this.context = context;
        this.status = status;
    }

    public Context context() { return context; }
    public Status status() { return status; }

    public enum Context {
        MAIN_MENU, ACCOUNT_INQUIRY, WITHDRAWAL, DEPOSIT, UNKNOWN
    }

    public enum Status {
        ACTIVE, INACTIVE, TIMED_OUT, ERROR
    }
}
