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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String providedTellerId;
    private String providedTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state: authenticated is required for success
        aggregate.markAuthenticated();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        providedTellerId = "teller-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        providedTerminalId = "term-A";
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Default constructor sets isAuthenticated to false, which is the violation.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markStale(); // Sets last activity too far back
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // The violation will be triggered by providing a null/blank terminalId in the When step context
        // or specifically setting up the aggregate to expect it. 
        // However, the invariant check is in the command execution based on command data or aggregate state.
        // We will simulate this by passing an invalid terminal ID contextually.
        providedTerminalId = ""; // Invalid context
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), providedTellerId, providedTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(providedTellerId, event.tellerId());
        assertEquals(providedTerminalId, event.terminalId());
        assertTrue(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect an IllegalStateException or IllegalArgumentException based on implementation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}