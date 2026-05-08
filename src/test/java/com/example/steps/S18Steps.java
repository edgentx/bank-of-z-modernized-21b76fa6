package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> result;
    private Exception caughtException;

    private String tellerId;
    private String terminalId;
    private boolean isAuthenticated;
    private boolean isExpired;
    private String navigationState;

    // In-memory repository mock behavior (implied by constructor)
    // Usually injected, but here we instantiate directly for domain testing.

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        caughtException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-123");
        isAuthenticated = false; // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        isAuthenticated = true; // Valid
        isExpired = true; // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-123");
        isAuthenticated = true; // Valid
        isExpired = false; // Valid
        navigationState = "INVALID_STATE"; // Violation
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "terminal-T1";
    }

    // We set defaults for valid scenarios here to ensure clean state unless overridden
    @Given("a valid TellerSession aggregate")
    public void setup_defaults_for_valid() {
        isAuthenticated = true;
        isExpired = false;
        navigationState = "HOME";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Ensure defaults if not explicitly set by violation Given
        if (tellerId == null) tellerId = "teller-001";
        if (terminalId == null) terminalId = "terminal-T1";
        // If not set, assume valid for the positive case
        if (navigationState == null) navigationState = "HOME";

        StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(),
                tellerId,
                terminalId,
                isAuthenticated,
                isExpired,
                navigationState
        );

        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should emit exactly one event");
        assertTrue(result.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) result.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("terminal-T1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // The specification says "rejected with a domain error", typically implemented as IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Expected domain error (IllegalArgumentException/IllegalStateException), got: " + caughtException.getClass().getSimpleName());
    }
}