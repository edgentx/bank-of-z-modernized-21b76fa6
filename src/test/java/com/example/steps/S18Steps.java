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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private boolean isAuthenticated;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "term-42";
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "sess-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-42";
        this.isAuthenticated = false; // Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-42";
        this.isAuthenticated = true;
        // Simulate an old session that timed out
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.setLastActivityAt(past);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "sess-nav-error";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.tellerId = "teller-01";
        this.terminalId = "term-42";
        this.isAuthenticated = true;
        // Simulate being stuck in a transaction context (e.g., TXN_DEPOSIT)
        aggregate.setNavigationState("TXN_DEPOSIT_IN_PROGRESS");
    }

    // --- Action ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default authentication to true if not set by violation scenario
        // Note: In a valid scenario, isAuthenticated defaults to false, so we must explicitly set true
        // However, the 'valid' Given steps didn't set it. We assume valid = authenticated.
        // We check if it's the valid scenario (no exception thrown yet) and set auth=true if needed.
        if (aggregate.getId().equals("sess-123") && !isAuthenticated) {
            isAuthenticated = true;
        }

        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultEvents, "Result events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected a domain error exception, but command succeeded");
        // In Java domain logic, these are typically IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(
                thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException,
                "Expected IllegalArgumentException or IllegalStateException, got: " + thrownException.getClass().getSimpleName()
        );
    }
}
