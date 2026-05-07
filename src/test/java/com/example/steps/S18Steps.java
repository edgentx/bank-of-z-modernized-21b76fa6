package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception domainException;

    // Helper to build a default valid command
    private StartSessionCmd.Builder defaultCommand() {
        return new StartSessionCmd.Builder(
                "session-123",
                "teller-001",
                "term-42"
        );
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        domainException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in context of command construction in 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in context of command construction in 'When' step
    }

    // --- Negative Scenarios Setup ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-unauth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_with_stale_activity() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_with_invalid_navigation() {
        aggregate = new TellerSessionAggregate("session-nav-error");
    }

    // --- Execution ---

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Determine command state based on aggregate state (derived from Givens)
        if (aggregate != null) {
            String id = aggregate.id();
            StartSessionCmd.Builder builder = new StartSessionCmd.Builder(id, "teller-1", "term-1");

            // Match specific aggregate IDs to constraints to simulate violations
            if ("session-unauth".equals(id)) {
                builder.authenticated(false);
            } else if ("session-timeout".equals(id)) {
                // Simulate activity 20 minutes ago (Timeout is 15)
                builder.lastActivityAt(Instant.now().minusSeconds(1200));
            } else if ("session-nav-error".equals(id)) {
                builder.navigationValid(false);
            }
            // Defaults for 'valid' path (session-123) are true/now

            command = builder.build();
        }

        try {
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            domainException = e;
        }
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException, "Expected an exception to be thrown");
        assertTrue(domainException instanceof IllegalStateException, "Expected IllegalStateException");
    }

    // Command Builder Helper Class
    private static class StartSessionCmd {
        private final String sessionId;
        private final String tellerId;
        private final String terminalId;
        private final boolean isAuthenticated;
        private final boolean isNavigationValid;
        private final Instant lastActivityAt;

        public StartSessionCmd(String sessionId, String tellerId, String terminalId, boolean isAuthenticated, boolean isNavigationValid, Instant lastActivityAt) {
            this.sessionId = sessionId;
            this.tellerId = tellerId;
            this.terminalId = terminalId;
            this.isAuthenticated = isAuthenticated;
            this.isNavigationValid = isNavigationValid;
            this.lastActivityAt = lastActivityAt;
        }

        public String sessionId() { return sessionId; }
        public String tellerId() { return tellerId; }
        public String terminalId() { return terminalId; }
        public boolean isAuthenticated() { return isAuthenticated; }
        public boolean isNavigationValid() { return isNavigationValid; }
        public Instant lastActivityAt() { return lastActivityAt; }

        static class Builder {
            private final String sessionId;
            private final String tellerId;
            private final String terminalId;
            private boolean isAuthenticated = true; // Default valid
            private boolean isNavigationValid = true; // Default valid
            private Instant lastActivityAt = Instant.now(); // Default valid

            public Builder(String sessionId, String tellerId, String terminalId) {
                this.sessionId = sessionId;
                this.tellerId = tellerId;
                this.terminalId = terminalId;
            }

            public Builder authenticated(boolean auth) { this.isAuthenticated = auth; return this; }
            public Builder navigationValid(boolean valid) { this.isNavigationValid = valid; return this; }
            public Builder lastActivityAt(Instant time) { this.lastActivityAt = time; return this; }

            public StartSessionCmd build() {
                return new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, isNavigationValid, lastActivityAt);
            }
        }
    }
}