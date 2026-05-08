package com.example.domain;

import java.time.Duration;

public class StartSessionCmd {
    private String tellerId;
    private String terminalId;
    private Duration timeoutConfig = Duration.ofMinutes(30); // Default
    private String initialContext = "HOME"; // Default

    public String getTellerId() {
        return tellerId;
    }

    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Duration getTimeoutConfig() {
        return timeoutConfig;
    }

    public void setTimeoutConfig(Duration timeoutConfig) {
        this.timeoutConfig = timeoutConfig;
    }

    public String getInitialContext() {
        return initialContext;
    }

    public void setInitialContext(String initialContext) {
        this.initialContext = initialContext;
    }
}
